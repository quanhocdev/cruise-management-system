package com.project.booking.service.account;

import com.project.booking.client.PaymentClient;
import com.project.booking.dto.payment.BillCollectionResponse;
import com.project.booking.service.command.CommandException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class FinanceReportService {
    private final JdbcTemplate jdbc;
    private final PaymentClient payment;
    private final TransactionTemplate snapshot;
    public FinanceReportService(JdbcTemplate jdbc,PaymentClient payment,PlatformTransactionManager manager) {
        this.jdbc=jdbc;this.payment=payment;this.snapshot=new TransactionTemplate(manager);
        snapshot.setReadOnly(true);snapshot.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
    }
    public record Charges(long bookings,BigDecimal gross,BigDecimal voided,BigDecimal refunded,BigDecimal net) {}
    public record Settlement(long billCount,BigDecimal billed,long unsettledBillCount,boolean paymentAvailable,
        BigDecimal collected,BigDecimal returned,BigDecimal refundPending,BigDecimal netCollected,
        BigDecimal unpaidBillAmount,long unverifiedPaidBills,long reviewCount) {}
    public record BillRow(Long billId,Long bookingId,String bookingCode,String billCode,String status,
        BigDecimal amount,LocalDateTime createdAt,BigDecimal collected,BigDecimal returned,boolean needsReview) {}
    public record BillPage(int page,int size,long totalItems,List<BillRow> items) {}
    public record Report(UUID tourId,String currency,Instant generatedAt,Charges charges,Settlement settlement,BillPage bills) {}
    private record Local(Charges charges,List<BillRow> bills) {}

    public Report read(UUID tourId,int page,int size,String status,String sort) {
        if(tourId==null || page<0 || size<1 || size>100 || !Set.of("ALL","PAID","UNPAID").contains(status)
                || !Set.of("newest","oldest","amountDesc").contains(sort))
            throw new CommandException("INVALID_REPORT_QUERY","Bộ lọc hoặc phân trang không hợp lệ",HttpStatus.BAD_REQUEST);
        Instant at=Instant.now();
        Local local=Objects.requireNonNull(snapshot.execute(s->readLocal(tourId)));
        var collections=new HashMap<Long,BillCollectionResponse>();
        boolean available=true;
        var ids=local.bills().stream().map(BillRow::billId).toList();
        try {
            for(int i=0;i<ids.size();i+=100) {
                var batch=ids.subList(i,Math.min(i+100,ids.size()));
                var rows=payment.getBillCollections(batch);
                if(rows==null || rows.size()!=batch.size())throw new RestClientException("Incomplete collection summary");
                for(var row:rows) {
                    if(row==null || !batch.contains(row.billId()) || row.collected()==null || row.returned()==null
                            || row.refundPending()==null || collections.putIfAbsent(row.billId(),row)!=null)
                        throw new RestClientException("Invalid collection summary");
                }
            }
        } catch(RestClientException ex) {available=false;collections.clear();}
        BigDecimal billed=BigDecimal.ZERO,collected=BigDecimal.ZERO,returned=BigDecimal.ZERO,pending=BigDecimal.ZERO,unpaid=BigDecimal.ZERO;
        long unsettled=0,unverified=0,reviews=0;
        var bookingCounts=new HashMap<Long,Integer>();
        local.bills().forEach(b->bookingCounts.merge(b.bookingId(),1,Integer::sum));
        var enriched=new ArrayList<BillRow>();
        for(var bill:local.bills()) {
            billed=billed.add(bill.amount());
            boolean paid="PAID".equals(bill.status());
            if(!paid)unsettled++;
            var p=collections.get(bill.billId());
            boolean review=bookingCounts.get(bill.bookingId())>1;
            if(available) {
                collected=collected.add(p.collected());returned=returned.add(p.returned());pending=pending.add(p.refundPending());
                if(!paid)unpaid=unpaid.add(bill.amount().subtract(p.collected()).max(BigDecimal.ZERO));
                if(paid && bill.amount().signum()>0 && p.collected().compareTo(bill.amount())!=0)unverified++;
                review|=p.needsReview() || (paid && p.collected().compareTo(bill.amount())!=0)
                    || (!paid && p.collected().signum()>0) || p.collected().compareTo(bill.amount())>0;
            }
            if(review)reviews++;
            enriched.add(new BillRow(bill.billId(),bill.bookingId(),bill.bookingCode(),bill.billCode(),bill.status(),bill.amount(),
                bill.createdAt(),p==null?null:p.collected(),p==null?null:p.returned(),review));
        }
        Comparator<BillRow> order=Comparator.comparing(BillRow::createdAt).thenComparing(BillRow::billId);
        if(sort.equals("newest"))order=order.reversed();
        if(sort.equals("amountDesc"))order=Comparator.comparing(BillRow::amount).reversed().thenComparing(BillRow::billId);
        var filtered=enriched.stream().filter(b->status.equals("ALL") || status.equals("PAID")=="PAID".equals(b.status())).sorted(order).toList();
        var settlement=new Settlement(ids.size(),billed,unsettled,available,available?collected:null,available?returned:null,
            available?pending:null,available?collected.subtract(returned):null,available?unpaid:null,unverified,reviews);
        return new Report(tourId,"VND",at,local.charges(),settlement,
            new BillPage(page,size,filtered.size(),filtered.stream().skip((long)page*size).limit(size).toList()));
    }

    private Local readLocal(UUID tourId) {
        var totals=jdbc.queryForMap(UsageLedgerSql.ALL+"""
            , scoped_bookings AS (SELECT id FROM booking.bookings WHERE tour_id=?), amounts AS (
              SELECT e.entry_type,e.amount FROM booking.ledger_entries e JOIN scoped_bookings b ON b.id=e.booking_id
              UNION ALL
              SELECT 'CHARGE',u.final_amount FROM usage_lines u JOIN booking.booking_passengers bp ON bp.id=u.booking_passenger_id
                JOIN scoped_bookings b ON b.id=bp.booking_id
              WHERE NOT EXISTS (SELECT 1 FROM booking.ledger_entries e WHERE e.source_type=u.type
                AND e.source_usage_id=u.id AND e.entry_type='CHARGE')
            )
            SELECT COALESCE(sum(amount) FILTER (WHERE entry_type='CHARGE'),0) AS gross,
                -COALESCE(sum(amount) FILTER (WHERE entry_type='VOID'),0) AS voided,
                -COALESCE(sum(amount) FILTER (WHERE entry_type='REFUND'),0) AS refunded,
                COALESCE(sum(amount),0) AS net,(SELECT count(*) FROM scoped_bookings) AS bookings FROM amounts
            """,tourId);
        var charges=new Charges(((Number)totals.get("bookings")).longValue(),(BigDecimal)totals.get("gross"),
            (BigDecimal)totals.get("voided"),(BigDecimal)totals.get("refunded"),(BigDecimal)totals.get("net"));
        var bills=jdbc.query("""
            SELECT x.id,x.booking_id,b.booking_code,x.bill_code,x.status,x.total_amount,x.created_at
            FROM booking.bills x JOIN booking.bookings b ON b.id=x.booking_id WHERE b.tour_id=?
            """,(r,n)->new BillRow(r.getLong("id"),r.getLong("booking_id"),r.getString("booking_code"),r.getString("bill_code"),
                r.getString("status"),r.getBigDecimal("total_amount"),r.getObject("created_at",LocalDateTime.class),null,null,false),tourId);
        return new Local(charges,bills);
    }
}
