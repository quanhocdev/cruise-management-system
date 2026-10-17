package com.project.booking.service.account;

import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.service.command.CommandException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

/** Captures new usages in an append-only journal without replacing legacy Bill calculation. */
@Service
public class OnboardAccountService {
    private final JdbcTemplate jdbc;
    public OnboardAccountService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public void openForCheckIn(Booking booking) {
        jdbc.update("""
            INSERT INTO booking.onboard_accounts(booking_id, tour_id, status, credit_limit)
            SELECT ?, ?, CASE
              WHEN EXISTS (SELECT 1 FROM booking.bills WHERE booking_id=? AND status='PAID') THEN 'CLOSED'
              WHEN EXISTS (SELECT 1 FROM booking.bills WHERE booking_id=? AND status='PENDING_PAYMENT') THEN 'SETTLING'
              ELSE 'OPEN' END, (SELECT credit_limit FROM booking.tour_spending_policies WHERE tour_id=?)
            ON CONFLICT (booking_id) DO NOTHING
            """, booking.getId(), booking.getTourId(), booking.getId(), booking.getId(), booking.getTourId());
    }

    @Transactional
    public void lockForUsage(Booking booking) {
        if (booking.getStatus() != com.project.booking.model.enums.BookingStatus.CONFIRMED) {
            throw new CommandException("BOOKING_NOT_CONFIRMED", "Booking chưa được xác nhận", HttpStatus.CONFLICT);
        }
        String status = lockAccount(booking);
        if (!"OPEN".equals(status)) {
            throw new CommandException("CLOSED".equals(status) ? "ACCOUNT_CLOSED" : "ACCOUNT_SETTLING",
                    "Tài khoản đã chốt hoặc đang quyết toán", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public String lockAccount(Booking booking) {
        openForCheckIn(booking);
        return jdbc.queryForObject("SELECT status FROM booking.onboard_accounts WHERE booking_id=? FOR UPDATE",
                String.class, booking.getId());
    }

    /** Must run before saving usage, in the same transaction as quota and journal writes. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void authorizeCharge(Booking booking, BigDecimal amount) {
        if (amount == null || amount.signum() < 0) throw new IllegalArgumentException("Invalid charge amount");
        lockForUsage(booking);
        if (amount.signum() == 0) return;
        // Accounts opened before a tour policy existed can adopt its first configured limit.
        jdbc.update("""
            UPDATE booking.onboard_accounts a SET credit_limit=p.credit_limit, updated_at=CURRENT_TIMESTAMP
            FROM booking.tour_spending_policies p
            WHERE a.booking_id=? AND a.credit_limit IS NULL AND p.tour_id=a.tour_id
            """, booking.getId());
        BigDecimal limit = jdbc.queryForObject("SELECT credit_limit FROM booking.onboard_accounts WHERE booking_id=?",
                BigDecimal.class, booking.getId());
        if (limit == null) {
            throw new CommandException("CREDIT_LIMIT_NOT_CONFIGURED",
                    "Chưa cấu hình hạn mức chi tiêu. Vui lòng liên hệ bộ phận tài chính.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        BigDecimal balance = jdbc.queryForObject(UsageLedgerSql.BALANCE, BigDecimal.class, booking.getId(), booking.getId());
        if (balance.add(amount.setScale(2, RoundingMode.HALF_UP)).compareTo(limit) > 0) {
            throw new CommandException("CREDIT_LIMIT_EXCEEDED",
                    "Khoản phí vượt hạn mức chi tiêu còn lại. Vui lòng liên hệ bộ phận tài chính.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Transactional
    public void recordCharge(BookingPassenger passenger, String source, Long usageId, BigDecimal amount,
                             int quantity, UUID benefitId, long benefitQuantity, UUID requestId) {
        if (passenger.getStatus() != com.project.booking.model.enums.BookingPassengerStatus.CHECKED_IN) {
            throw new CommandException("PASSENGER_NOT_CHECKED_IN", "Hành khách chưa check-in", HttpStatus.CONFLICT);
        }
        if (amount == null || amount.signum() < 0 || quantity < 1 || benefitQuantity < 0 || benefitQuantity > quantity) {
            throw new IllegalArgumentException("Invalid usage amounts");
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = auth == null ? null : auth.getName();
        if (auth instanceof JwtAuthenticationToken jwt && jwt.getTokenAttributes().get("userId") != null) {
            actor = "user:" + jwt.getTokenAttributes().get("userId");
        }
        jdbc.update("""
            INSERT INTO booking.ledger_entries(booking_id, booking_passenger_id, room_id, entry_type,
              amount, source_type, source_usage_id, quantity, package_benefit_id, benefit_quantity, actor_id, client_request_id)
            VALUES (?, ?, ?, 'CHARGE', ?, ?, ?, ?, ?, ?, ?, ?)
            """, passenger.getBooking().getId(), passenger.getId(), passenger.getRoomId(),
                amount.setScale(2, RoundingMode.HALF_UP), source, usageId, quantity, benefitId,
                Math.toIntExact(benefitQuantity), actor, requestId);
    }

    @Transactional
    public void beginSettlement(Booking booking) {
        openForCheckIn(booking);
        jdbc.queryForObject("SELECT status FROM booking.onboard_accounts WHERE booking_id=? FOR UPDATE", String.class, booking.getId());
        jdbc.update("UPDATE booking.onboard_accounts SET status='SETTLING', updated_at=CURRENT_TIMESTAMP WHERE booking_id=? AND status='OPEN'", booking.getId());
    }

    @Transactional
    public void markClosed(Long bookingId) {
        jdbc.update("UPDATE booking.onboard_accounts SET status='CLOSED', updated_at=CURRENT_TIMESTAMP WHERE booking_id=?", bookingId);
    }
}
