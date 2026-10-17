package com.project.booking.service.account;

import com.project.booking.service.command.CommandException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SpendingSummaryService {
    private final JdbcTemplate jdbc;
    public SpendingSummaryService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String LINES = UsageLedgerSql.SCOPED;

    public record Line(String type, Long usageId, Long bookingPassengerId, UUID targetId, int quantity,
                       BigDecimal unitPrice, BigDecimal discountAmount, BigDecimal finalAmount, LocalDateTime usedAt) {}
    public record Summary(Long bookingId, String currency, String accountStatus, BigDecimal creditLimit,
                          BigDecimal totalCharges, BigDecimal capturedLedgerTotal, boolean ledgerComplete,
                          long totalItems, int page, int size, List<Line> items) {}

    @Transactional(readOnly=true, isolation=Isolation.REPEATABLE_READ)
    public Summary getMine(Long bookingId, Long userId, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new CommandException("INVALID_PAGE", "page >= 0 and size between 1 and 100 required", HttpStatus.BAD_REQUEST);
        }
        if (userId == null || jdbc.queryForObject("SELECT count(*) FROM booking.bookings WHERE id=? AND created_by_user_id=?",
                Long.class, bookingId, userId) == 0) {
            throw new CommandException("BOOKING_NOT_FOUND", "Không tìm thấy booking của bạn", HttpStatus.NOT_FOUND);
        }
        var accounts = jdbc.queryForList("""
            SELECT a.status, COALESCE(a.credit_limit,p.credit_limit) AS credit_limit
            FROM booking.onboard_accounts a LEFT JOIN booking.tour_spending_policies p ON p.tour_id=a.tour_id
            WHERE a.booking_id=?
            """, bookingId);
        String status = accounts.isEmpty() ? "NOT_OPENED" : (String) accounts.get(0).get("status");
        BigDecimal limit = accounts.isEmpty() ? null : (BigDecimal) accounts.get(0).get("credit_limit");
        var totals = jdbc.queryForMap(LINES + "SELECT count(*) AS count, COALESCE(sum(final_amount),0) AS total FROM scoped", bookingId);
        var captured = jdbc.queryForMap("""
            SELECT count(*) AS count, COALESCE(sum(amount),0) AS total
            FROM booking.ledger_entries WHERE booking_id=? AND entry_type='CHARGE'
            """, bookingId);
        long totalItems = ((Number) totals.get("count")).longValue();
        BigDecimal total = (BigDecimal) totals.get("total");
        BigDecimal journalTotal = (BigDecimal) captured.get("total");
        boolean complete = totalItems == ((Number) captured.get("count")).longValue()
                && total.compareTo(journalTotal) == 0
                && Boolean.TRUE.equals(jdbc.queryForObject(LINES + """
                    SELECT NOT EXISTS (
                      SELECT 1 FROM scoped u WHERE NOT EXISTS (
                        SELECT 1 FROM booking.ledger_entries e
                        WHERE e.booking_id=? AND e.source_type=u.type AND e.source_usage_id=u.id
                          AND e.entry_type='CHARGE' AND e.booking_passenger_id=u.booking_passenger_id
                          AND e.amount=u.final_amount
                      )
                    )
                    """, Boolean.class, bookingId, bookingId));
        var lines = jdbc.query(LINES + "SELECT * FROM scoped ORDER BY used_at DESC, type, id DESC LIMIT ? OFFSET ?",
                (row, n) -> new Line(row.getString("type"), row.getLong("id"), row.getLong("booking_passenger_id"),
                        row.getObject("target_id", UUID.class), row.getInt("quantity"), row.getBigDecimal("unit_price"),
                        row.getBigDecimal("discount_amount"), row.getBigDecimal("final_amount"),
                        row.getObject("used_at", LocalDateTime.class)), bookingId, size, (long)page * size);
        return new Summary(bookingId, "VND", status, limit, total, journalTotal, complete, totalItems, page, size, lines);
    }
}
