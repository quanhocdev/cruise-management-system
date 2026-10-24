package com.project.booking.service.finance.bill;

import com.project.booking.client.PaymentClient;
import com.project.booking.dto.payment.*;
import com.project.booking.service.command.CommandException;
import com.project.common.event.BillPaidEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
public class BillReadService {
    private final JdbcTemplate jdbc;
    private final PaymentClient payment;
    private final RestClient notification;
    public BillReadService(JdbcTemplate jdbc,PaymentClient payment,RestClient.Builder builder,
            @Value("${notification-service.url:http://notification-service:8086}") String url,
            @Value("${internal.api-key:${INTERNAL_API_KEY:}}") String key) {
        this.jdbc=jdbc; this.payment=payment;
        this.notification=builder.clone().baseUrl(url).defaultHeader("X-Internal-Api-Key",key).build();
    }
    public record Line(Long id,Long bookingPassengerId,String passengerName,String usageType,Long usageId,
                       int quantity,BigDecimal unitPrice,BigDecimal discountAmount,BigDecimal finalAmount) {}
    public record View(Long bookingId,String bookingCode,Long billId,String billCode,String billStatus,
                       String accountStatus,String currency,BigDecimal totalAmount,LocalDateTime createdAt,
                       List<Line> items,List<BillPaymentResponse> payments,boolean paymentAvailable,String paymentState) {}

    public void authorize(Long id,UUID owner) {
        if(owner==null || jdbc.queryForObject("SELECT count(*) FROM booking.bookings WHERE id=? AND created_by_user_id=?",Long.class,id,owner)!=1)
            throw new CommandException("BOOKING_NOT_FOUND","Không tìm thấy booking của bạn",HttpStatus.NOT_FOUND);
    }
    private Map<String,Object> header(Long id) {
        var rows=jdbc.queryForList("""
            SELECT b.id,b.booking_code,b.created_by_user_id,b.primary_contact_name,b.primary_contact_email,
                   b.number_passengers,b.total_amount AS booking_amount,
                   x.id AS bill_id,x.bill_code,x.total_amount,x.status,x.created_at,x.updated_at,
                   COALESCE(a.status,'NOT_OPENED') AS account_status
            FROM booking.bookings b LEFT JOIN booking.bills x ON x.booking_id=b.id
            LEFT JOIN booking.onboard_accounts a ON a.booking_id=b.id WHERE b.id=? ORDER BY x.created_at DESC
            """,id);
        if(rows.isEmpty()) throw new CommandException("BOOKING_NOT_FOUND","Không tìm thấy booking",HttpStatus.NOT_FOUND);
        if(rows.size()>1) throw new CommandException("AMBIGUOUS_BILLS","Booking có nhiều bill, cần Finance kiểm tra",HttpStatus.CONFLICT);
        return rows.get(0);
    }
    private List<Line> lines(Long bill) {
        return jdbc.query("SELECT * FROM booking.bill_items WHERE bill_id=? ORDER BY id",
            (r,n)->new Line(r.getLong("id"),r.getLong("booking_passenger_id"),r.getString("passenger_name"),
                r.getString("usage_type"),r.getLong("usage_id"),r.getInt("quantity"),r.getBigDecimal("unit_price"),
                r.getBigDecimal("discount_amount"),r.getBigDecimal("final_amount")),bill);
    }
    public View get(Long id) {
        var h=header(id); Long bill=(Long)h.get("bill_id");
        List<BillPaymentResponse> attempts=List.of(); boolean available=true;
        if(bill!=null && ((BigDecimal)h.get("total_amount")).signum()>0) {
            try { attempts=payment.getBillPayments(bill); }
            catch(org.springframework.web.client.RestClientException e) { available=false; }
        }
        String state=bill==null?"NOT_ISSUED":"PAID".equals(h.get("status"))?"PAID":!available?"UNAVAILABLE":attempts.isEmpty()?"NOT_STARTED":"FAILED";
        if(bill!=null && !"PAID".equals(h.get("status")) && available) {
            if(attempts.stream().anyMatch(p->"SUCCESS".equals(p.status())||"REFUNDED".equals(p.status()))) state="VERIFYING";
            else if(attempts.stream().anyMatch(p->"PENDING".equals(p.status()))) {
                state=attempts.stream().filter(p->"PENDING".equals(p.status())).anyMatch(p->p.expiresAt()!=null && p.expiresAt().isAfter(Instant.now()))?"PENDING":"UNKNOWN";
            }
            else if(attempts.stream().anyMatch(p->"EXPIRED".equals(p.status()))) state="UNKNOWN";
        }
        return new View(id,(String)h.get("booking_code"),bill,(String)h.get("bill_code"),(String)h.get("status"),
                (String)h.get("account_status"),"VND",(BigDecimal)h.get("total_amount"),
                h.get("created_at")==null?null:((java.sql.Timestamp)h.get("created_at")).toLocalDateTime(),
                bill==null?List.of():lines(bill),attempts,available,state);
    }
    public ResponseEntity<BillPaymentResponse> pay(Long id,UUID key) {
        if(key==null || key.version()!=4 || key.variant()!=2) throw new CommandException("INVALID_CLIENT_REQUEST_ID","clientRequestId phải là UUID v4",HttpStatus.BAD_REQUEST);
        var h=header(id);
        if(h.get("bill_id")==null) throw new CommandException("BILL_NOT_ISSUED","Cần chốt bill trước",HttpStatus.UNPROCESSABLE_ENTITY);
        if(((BigDecimal)h.get("total_amount")).signum()<=0) throw new CommandException("NO_PAYMENT_REQUIRED","Bill không phát sinh thanh toán",HttpStatus.UNPROCESSABLE_ENTITY);
        if("PAID".equals(h.get("status")) && payment.getBillPayments((Long)h.get("bill_id")).stream()
                .noneMatch(p->"SUCCESS".equals(p.status()) || "REFUNDED".equals(p.status())))
            throw new CommandException("BILL_ALREADY_PAID","Bill đã quyết toán, không tạo thêm thanh toán.",HttpStatus.CONFLICT);
        var result=payment.createCommand(new BillPaymentRequest((Long)h.get("bill_id"),(UUID)h.get("created_by_user_id"),(BigDecimal)h.get("total_amount"),key));
        return ResponseEntity.status(201).header("Idempotent-Replay",result.getHeaders().getFirst("Idempotent-Replay")==null?"false":result.getHeaders().getFirst("Idempotent-Replay")).body(result.getBody());
    }
    public byte[] receipt(Long id) {
        var h=header(id);
        if(!"PAID".equals(h.get("status"))) throw new CommandException("BILL_NOT_PAID","Biên nhận có sau khi quyết toán hoàn tất",HttpStatus.CONFLICT);
        Long bill=(Long)h.get("bill_id");
        var event=new BillPaidEvent(bill,(String)h.get("bill_code"),(BigDecimal)h.get("total_amount"),"PAID",
            ((java.sql.Timestamp)h.get("created_at")).toLocalDateTime(),((java.sql.Timestamp)h.get("updated_at")).toLocalDateTime(),
            id,(String)h.get("booking_code"),((Number)h.get("number_passengers")).intValue(),(BigDecimal)h.get("booking_amount"),
            (UUID)h.get("created_by_user_id"),(String)h.get("primary_contact_email"),(String)h.get("primary_contact_name"),List.of(),
            lines(bill).stream().map(l->new BillPaidEvent.BillItemDto(l.id(),l.bookingPassengerId(),l.usageType(),l.usageId(),l.quantity(),l.unitPrice(),l.discountAmount(),l.finalAmount())).toList());
        return notification.post().uri("/internal/bill-receipts").body(event).retrieve().body(byte[].class);
    }
}
