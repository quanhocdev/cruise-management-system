package com.project.booking.service.account;

import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LedgerAdjustmentReader {
    private final JdbcTemplate jdbc;
    public LedgerAdjustmentReader(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public BigDecimal amount(long passengerId, String source, long usageId) {
        return jdbc.queryForObject("""
            SELECT COALESCE(sum(amount),0) FROM booking.ledger_entries
            WHERE booking_passenger_id=? AND source_type=? AND source_usage_id=? AND entry_type IN ('VOID','REFUND')
            """, BigDecimal.class, passengerId,source,usageId);
    }
    public List<BillItem> billItems(Bill bill, long passengerId) {
        return jdbc.query("""
            SELECT id,entry_type,amount FROM booking.ledger_entries
            WHERE booking_id=? AND booking_passenger_id=? AND entry_type IN ('VOID','REFUND') ORDER BY id
            """, (r,n) -> {
            var item=new BillItem(); item.setBill(bill); item.setBookingPassengerId(passengerId);
            item.setUsageType(r.getString("entry_type")); item.setUsageId(r.getLong("id")); item.setQuantity(1);
            item.setUnitPrice(r.getBigDecimal("amount")); item.setDiscountAmount(BigDecimal.ZERO); item.setFinalAmount(r.getBigDecimal("amount"));
            return item;
        }, bill.getBooking().getId(),passengerId);
    }
}
