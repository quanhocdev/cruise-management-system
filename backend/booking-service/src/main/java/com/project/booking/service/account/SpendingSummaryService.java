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

    private static final String LINES = """
        WITH usage_lines AS (
          SELECT 'PRODUCT' AS type, id, booking_passenger_id, product_tour_id AS target_id,
                 quantity, unit_price, discount_amount, final_amount, used_at FROM booking.product_usages
          UNION ALL
          SELECT 'SERVICE', id, booking_passenger_id, service_tour_id, 1,
                 unit_price, discount_amount, final_amount, used_at FROM booking.service_usages
          UNION ALL
          SELECT 'ACTIVITY', id, booking_passenger_id, activity_cruise_tour_id, quantity,
                 unit_price, discount_amount, final_amount, used_at FROM booking.activity_cruise_usages
          UNION ALL
          SELECT 'SHORE', id, booking_passenger_id, visit_tour_id, quantity,
                 unit_price, discount_amount, final_amount, used_at FROM booking.activity_visit_usages
        ), scoped AS (
          SELECT u.* FROM usage_lines u JOIN booking.booking_passengers bp ON bp.id=u.booking_passenger_id
          WHERE bp.booking_id=?
        )
        """;

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
        var accounts = jdbc.queryForList("SELECT status, credit_limit FROM booking.onboard_accounts WHERE booking_id=?", bookingId);
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
        boolean complete = totalItems == ((Number) captured.get("count")).longValue() && total.compareTo(journalTotal) == 0;
        var lines = jdbc.query(LINES + "SELECT * FROM scoped ORDER BY used_at DESC, type, id DESC LIMIT ? OFFSET ?",
                (row, n) -> new Line(row.getString("type"), row.getLong("id"), row.getLong("booking_passenger_id"),
                        row.getObject("target_id", UUID.class), row.getInt("quantity"), row.getBigDecimal("unit_price"),
                        row.getBigDecimal("discount_amount"), row.getBigDecimal("final_amount"),
                        row.getObject("used_at", LocalDateTime.class)), bookingId, size, (long)page * size);
        return new Summary(bookingId, "VND", status, limit, total, journalTotal, complete, totalItems, page, size, lines);
    }
}
