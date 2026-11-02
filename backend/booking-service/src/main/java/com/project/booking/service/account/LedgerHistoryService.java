package com.project.booking.service.account;

import com.project.booking.service.command.CommandException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class LedgerHistoryService {
    private final JdbcTemplate jdbc;
    public LedgerHistoryService(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record Result(Long bookingId, int importedCount, BigDecimal balance) {}

    @Transactional
    public Result importMissing(Long bookingId) {
        // Do not create or reopen historical accounts from an assumed lifecycle.
        var accounts = jdbc.queryForList("SELECT status FROM booking.onboard_accounts WHERE booking_id=? FOR UPDATE", bookingId);
        if (accounts.isEmpty()) throw new CommandException("ACCOUNT_NOT_INITIALIZED", "Tài khoản chi tiêu chưa được khởi tạo", HttpStatus.UNPROCESSABLE_ENTITY);
        boolean invalid = Boolean.TRUE.equals(jdbc.queryForObject(UsageLedgerSql.SCOPED + """
            SELECT EXISTS (SELECT 1 FROM scoped WHERE final_amount IS NULL OR final_amount<0
              OR quantity IS NULL OR quantity<1 OR used_at IS NULL)
              OR EXISTS (SELECT 1 FROM scoped u JOIN booking.ledger_entries e
                ON e.source_type=u.type AND e.source_usage_id=u.id AND e.entry_type='CHARGE'
                WHERE e.booking_id<>? OR e.booking_passenger_id<>u.booking_passenger_id OR e.amount<>u.final_amount OR e.quantity<>u.quantity)
              OR EXISTS (SELECT 1 FROM booking.ledger_entries e WHERE e.booking_id=? AND e.entry_type='CHARGE'
                AND NOT EXISTS (SELECT 1 FROM scoped u WHERE u.type=e.source_type AND u.id=e.source_usage_id))
            """, Boolean.class, bookingId, bookingId, bookingId));
        if (invalid) throw new CommandException("HISTORY_RECONCILIATION_REQUIRED", "Lịch sử có chênh lệch, cần đối soát trước khi bổ sung", HttpStatus.UNPROCESSABLE_ENTITY);
        BigDecimal before = jdbc.queryForObject(UsageLedgerSql.BALANCE, BigDecimal.class, bookingId, bookingId);
        int added = jdbc.update(UsageLedgerSql.SCOPED + """
            INSERT INTO booking.ledger_entries(booking_id,booking_passenger_id,entry_type,amount,source_type,
              source_usage_id,quantity,benefit_quantity,origin,source_used_at)
            SELECT ?,u.booking_passenger_id,'CHARGE',u.final_amount,u.type,u.id,u.quantity,NULL,'LEGACY_USAGE',u.used_at
            FROM scoped u WHERE NOT EXISTS (
              SELECT 1 FROM booking.ledger_entries e WHERE e.source_type=u.type AND e.source_usage_id=u.id AND e.entry_type='CHARGE')
            ON CONFLICT (source_type,source_usage_id) WHERE entry_type='CHARGE' DO NOTHING
            """, bookingId, bookingId);
        BigDecimal after = jdbc.queryForObject(UsageLedgerSql.BALANCE, BigDecimal.class, bookingId, bookingId);
        if (before.compareTo(after) != 0) throw new IllegalStateException("Journal import changed account balance");
        return new Result(bookingId, added, after);
    }
}
