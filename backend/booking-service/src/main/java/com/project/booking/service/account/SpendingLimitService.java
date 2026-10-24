package com.project.booking.service.account;

import com.project.booking.repository.BookingRepository;
import com.project.booking.service.command.CommandException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpendingLimitService {
    private final JdbcTemplate jdbc;
    private final BookingRepository bookings;
    private final OnboardAccountService accounts;

    public SpendingLimitService(JdbcTemplate jdbc, BookingRepository bookings, OnboardAccountService accounts) {
        this.jdbc = jdbc;
        this.bookings = bookings;
        this.accounts = accounts;
    }

    public record Result(Long bookingId, BigDecimal creditLimit, BigDecimal spent, BigDecimal remaining) {}

    public record Account(Long bookingId, String bookingCode, String contactName, String status,
                          BigDecimal creditLimit, BigDecimal spent, long pendingHistoryCount, boolean canEdit) {}

    @Transactional(readOnly=true, isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public Account find(String bookingCode) {
        var booking = bookings.findByBookingCodeIgnoreCase(bookingCode.trim()).orElseThrow(() ->
                new CommandException("BOOKING_NOT_FOUND", "Không tìm thấy mã booking", HttpStatus.NOT_FOUND));
        Long id = booking.getId();
        var rows = jdbc.queryForList("""
            SELECT a.status,COALESCE(a.credit_limit,p.credit_limit) AS credit_limit
            FROM booking.onboard_accounts a LEFT JOIN booking.tour_spending_policies p ON p.tour_id=a.tour_id WHERE a.booking_id=?
            """, id);
        String status = rows.isEmpty() ? "NOT_OPENED" : (String)rows.get(0).get("status");
        BigDecimal limit = rows.isEmpty() ? null : (BigDecimal)rows.get(0).get("credit_limit");
        BigDecimal spent = jdbc.queryForObject(UsageLedgerSql.BALANCE, BigDecimal.class, id, id);
        long pending = jdbc.queryForObject(UsageLedgerSql.SCOPED + """
            SELECT count(*) FROM scoped u WHERE NOT EXISTS (SELECT 1 FROM booking.ledger_entries e
              WHERE e.source_type=u.type AND e.source_usage_id=u.id AND e.entry_type='CHARGE')
            """, Long.class, id);
        boolean checkedIn = jdbc.queryForObject("SELECT count(*) FROM booking.booking_passengers WHERE booking_id=? AND status='CHECKED_IN'", Long.class, id)>0;
        return new Account(id, booking.getBookingCode(), booking.getPrimaryContactName(), status, limit, spent, pending,
                checkedIn && booking.getStatus()==com.project.booking.model.enums.BookingStatus.CONFIRMED
                        && (status.equals("OPEN") || status.equals("NOT_OPENED")));
    }

    @Transactional
    public Result setLimit(Long bookingId, BigDecimal limit, String reason, UUID requestId, String actor) {
        if (limit == null || limit.signum() < 0 || limit.scale() > 2 || limit.precision() - limit.scale() > 17
                || reason == null || reason.isBlank() || reason.length() > 1000 || requestId == null || actor == null) {
            throw new CommandException("INVALID_SPENDING_LIMIT", "Hạn mức hoặc lý do không hợp lệ", HttpStatus.BAD_REQUEST);
        }
        var booking = bookings.findById(bookingId).orElseThrow(() ->
                new CommandException("BOOKING_NOT_FOUND", "Không tìm thấy booking", HttpStatus.NOT_FOUND));
        if (jdbc.queryForObject("SELECT count(*) FROM booking.booking_passengers WHERE booking_id=? AND status='CHECKED_IN'", Long.class, bookingId) == 0) {
            throw new CommandException("NO_CHECKED_IN_PASSENGER", "Booking chưa có khách check-in", HttpStatus.CONFLICT);
        }
        accounts.lockForUsage(booking);
        BigDecimal spent = jdbc.queryForObject(UsageLedgerSql.BALANCE, BigDecimal.class, bookingId, bookingId);
        if (limit.compareTo(spent) < 0) {
            throw new CommandException("LIMIT_BELOW_BALANCE", "Hạn mức không được thấp hơn số tiền đã sử dụng", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        jdbc.update("""
            INSERT INTO booking.spending_limit_changes(booking_id,previous_limit,new_limit,reason,actor_id,client_request_id)
            SELECT booking_id,credit_limit,?,?,?,? FROM booking.onboard_accounts WHERE booking_id=?
            """, limit, reason.trim(), actor, requestId, bookingId);
        jdbc.update("UPDATE booking.onboard_accounts SET credit_limit=?,updated_at=CURRENT_TIMESTAMP WHERE booking_id=?", limit, bookingId);
        return new Result(bookingId, limit.setScale(2), spent, limit.subtract(spent).setScale(2));
    }
}
