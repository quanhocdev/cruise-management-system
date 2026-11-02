package com.project.booking.service.account;

import com.project.booking.service.command.CommandException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerCorrectionService {
    private final JdbcTemplate jdbc;
    public LedgerCorrectionService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Entry(long id, long bookingPassengerId, String sourceType, long sourceUsageId,
                        BigDecimal amount, BigDecimal adjustedAmount, BigDecimal remainingAmount,
                        Integer benefitQuantity, int restoredQuota, OffsetDateTime createdAt,
                        boolean canVoid, boolean canRefund, String blockedReason) {}
    public record Page(long bookingId, String bookingCode, String status, long totalItems, int page, int size, List<Entry> items) {}
    public record Result(long entryId, long refEntryId, String type, BigDecimal amount, BigDecimal remainingAmount, int restoredQuota) {}

    private record Actor(String id, boolean finance) {}
    public void authorize(long entryId, String type) {
        Actor actor = actor();
        if (!actor.finance) {
            if (!"VOID".equals(type)) throw error("FINANCE_REQUIRED", HttpStatus.FORBIDDEN);
            if (jdbc.queryForObject("SELECT count(*) FROM booking.ledger_entries WHERE id=? AND entry_type='CHARGE' AND actor_id=? AND source_type IN ('PRODUCT','SERVICE')", Long.class, entryId, actor.id) == 0)
                throw error("CORRECTION_FORBIDDEN", HttpStatus.FORBIDDEN);
        }
    }
    private Actor actor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw error("AUTHENTICATION_REQUIRED", HttpStatus.UNAUTHORIZED);
        boolean finance = auth.getAuthorities().stream().anyMatch(a -> List.of("ROLE_FINANCE", "ROLE_ADMIN").contains(a.getAuthority()));
        if (!finance && auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_CONVENIENCE")))
            throw error("CORRECTION_FORBIDDEN", HttpStatus.FORBIDDEN);
        String id = auth instanceof JwtAuthenticationToken jwt && jwt.getTokenAttributes().get("userId") != null
                ? "user:" + jwt.getTokenAttributes().get("userId") : auth.getName();
        return new Actor(id, finance);
    }

    private static final String ENTRIES = """
        SELECT e.*, COALESCE(c.adjustment,0) AS adjustment, COALESCE(c.corrections,0) AS corrections,
          COALESCE(c.restored,0) AS restored, e.created_at BETWEEN clock_timestamp() - INTERVAL '10 minutes' AND clock_timestamp() AS recent
        FROM booking.ledger_entries e
        LEFT JOIN LATERAL (SELECT sum(amount) AS adjustment, count(*) AS corrections, sum(benefit_quantity) AS restored
          FROM booking.ledger_entries WHERE ref_entry_id=e.id AND entry_type IN ('VOID','REFUND')) c ON true
        """;

    @Transactional(readOnly=true, isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public Page list(String code, int page, int size) {
        Actor actor = actor();
        if (page < 0 || size < 1 || size > 100) throw error("INVALID_PAGE", HttpStatus.BAD_REQUEST);
        var bookings = jdbc.queryForList("SELECT b.id,b.booking_code,a.status FROM booking.bookings b LEFT JOIN booking.onboard_accounts a ON a.booking_id=b.id WHERE lower(b.booking_code)=lower(?)", code.trim());
        if (bookings.isEmpty()) throw error("BOOKING_NOT_FOUND", HttpStatus.NOT_FOUND);
        long booking = ((Number)bookings.get(0).get("id")).longValue();
        String status = (String)bookings.get(0).get("status");
        boolean hasBill = jdbc.queryForObject("SELECT count(*) FROM booking.bills WHERE booking_id=?", Long.class, booking) > 0;
        String filter = " WHERE e.booking_id=? AND e.entry_type='CHARGE'" + (actor.finance ? "" : " AND e.actor_id=? AND e.source_type IN ('PRODUCT','SERVICE')");
        Object[] args = actor.finance ? new Object[]{booking} : new Object[]{booking, actor.id};
        long count = jdbc.queryForObject("SELECT count(*) FROM booking.ledger_entries e" + filter, Long.class, args);
        var parameters = new java.util.ArrayList<Object>(List.of(args)); parameters.add(size); parameters.add((long)page * size);
        var entries = jdbc.query(ENTRIES + filter + " ORDER BY e.id DESC LIMIT ? OFFSET ?", (r,n) -> {
            BigDecimal amount = r.getBigDecimal("amount"), adjustment = r.getBigDecimal("adjustment");
            BigDecimal remaining = amount.add(adjustment);
            String blocked = !"OPEN".equals(status) || hasBill ? "ACCOUNT_NOT_OPEN" : r.getObject("benefit_quantity") == null ? "HISTORY_RECONCILIATION_REQUIRED"
                    : r.getLong("corrections") > 0 && remaining.signum() == 0 ? "ENTRY_ALREADY_REVERSED" : null;
            boolean voidable = blocked == null && r.getLong("corrections") == 0 && (actor.finance || r.getBoolean("recent"));
            if (blocked == null && !actor.finance && !voidable) blocked = "FINANCE_REQUIRED";
            return new Entry(r.getLong("id"), r.getLong("booking_passenger_id"), r.getString("source_type"), r.getLong("source_usage_id"),
                    amount, adjustment, remaining, (Integer)r.getObject("benefit_quantity"), r.getInt("restored"),
                    r.getObject("created_at", OffsetDateTime.class), voidable, blocked == null && actor.finance, blocked);
        }, parameters.toArray());
        return new Page(booking, (String)bookings.get(0).get("booking_code"), status == null ? "NOT_OPENED" : status, count, page, size, entries);
    }

    @Transactional
    public Result correct(long entryId, String type, BigDecimal requested, String reason, UUID requestId) {
        Actor actor = actor();
        if (!List.of("VOID", "REFUND").contains(type) || reason == null || reason.isBlank() || reason.length() > 1000 || requestId == null)
            throw error("INVALID_CORRECTION", HttpStatus.BAD_REQUEST);
        if (type.equals("REFUND") && !actor.finance) throw error("FINANCE_REQUIRED", HttpStatus.FORBIDDEN);
        var ids = jdbc.queryForList("SELECT booking_id FROM booking.ledger_entries WHERE id=? AND entry_type='CHARGE'", Long.class, entryId);
        if (ids.isEmpty()) throw error("ENTRY_NOT_FOUND", HttpStatus.NOT_FOUND);
        long booking = ids.get(0);
        String status = jdbc.queryForObject("SELECT status FROM booking.onboard_accounts WHERE booking_id=? FOR UPDATE", String.class, booking);
        var row = jdbc.queryForMap(ENTRIES + " WHERE e.id=?", entryId);
        if (!actor.finance && (!actor.id.equals(row.get("actor_id")) || !List.of("PRODUCT", "SERVICE").contains(row.get("source_type"))))
            throw error("CORRECTION_FORBIDDEN", HttpStatus.FORBIDDEN);
        if (!actor.finance && !Boolean.TRUE.equals(row.get("recent"))) throw error("VOID_WINDOW_EXPIRED", HttpStatus.FORBIDDEN);
        if (!"OPEN".equals(status)) throw error("ACCOUNT_NOT_OPEN", HttpStatus.CONFLICT);
        if (jdbc.queryForObject("SELECT count(*) FROM booking.bills WHERE booking_id=?", Long.class, booking) > 0)
            throw error("SETTLEMENT_ALREADY_STARTED", HttpStatus.CONFLICT);
        if (row.get("benefit_quantity") == null) throw error("HISTORY_RECONCILIATION_REQUIRED", HttpStatus.UNPROCESSABLE_ENTITY);
        BigDecimal original = (BigDecimal)row.get("amount"), remaining = original.add((BigDecimal)row.get("adjustment"));
        long corrections = ((Number)row.get("corrections")).longValue();
        if (corrections > 0 && (type.equals("VOID") || remaining.signum() == 0)) throw error("ENTRY_ALREADY_CORRECTED", HttpStatus.CONFLICT);
        BigDecimal amount;
        if (type.equals("VOID")) {
            if (requested != null) throw error("VOID_AMOUNT_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            amount = remaining;
        } else {
            amount = requested;
            if (amount == null || amount.signum() < 0 || amount.scale() > 2 || amount.precision()-amount.scale() > 17
                    || (amount.signum() == 0 && original.signum() != 0)) throw error("INVALID_REFUND_AMOUNT", HttpStatus.BAD_REQUEST);
            if (amount.compareTo(remaining) > 0) throw error("REFUND_EXCEEDS_REMAINING", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        BigDecimal after = remaining.subtract(amount);
        int restore = after.signum() == 0 ? ((Number)row.get("benefit_quantity")).intValue() - ((Number)row.get("restored")).intValue() : 0;
        if (restore < 0) throw error("HISTORY_RECONCILIATION_REQUIRED", HttpStatus.UNPROCESSABLE_ENTITY);
        if (restore > 0 && jdbc.update("""
            UPDATE booking.benefit_consumptions SET used_quantity=used_quantity-?,updated_at=CURRENT_TIMESTAMP
            WHERE booking_id=? AND package_benefit_id=? AND used_quantity>=?
            """, restore, booking, row.get("package_benefit_id"), restore) != 1)
            throw error("QUOTA_RECONCILIATION_REQUIRED", HttpStatus.UNPROCESSABLE_ENTITY);
        long id = jdbc.queryForObject("""
            INSERT INTO booking.ledger_entries(booking_id,booking_passenger_id,room_id,entry_type,amount,source_type,source_usage_id,
              quantity,package_benefit_id,benefit_quantity,actor_id,client_request_id,ref_entry_id,reason)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id
            """, Long.class, booking, row.get("booking_passenger_id"), row.get("room_id"), type, amount.negate(), row.get("source_type"),
                row.get("source_usage_id"), row.get("quantity"), row.get("package_benefit_id"), restore, actor.id, requestId, entryId, reason.trim());
        return new Result(id, entryId, type, amount.negate().setScale(2), after.setScale(2), restore);
    }

    private static CommandException error(String code, HttpStatus status) { return new CommandException(code, code, status); }
}
