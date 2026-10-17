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
