package com.project.payment.refund;

import static com.project.payment.refund.RefundModels.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@DependsOn("paymentRefundSchemaInitializer")
public class RefundStore {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;

    public RefundStore(JdbcTemplate jdbc, PlatformTransactionManager manager) {
        this.jdbc = jdbc;
        this.tx = new TransactionTemplate(manager);
    }

    public Accepted create(long paymentId, UUID actorId, Request request) {
        validate(request);
        String hash = hash(paymentId, request);
        return tx.execute(s -> {
            // The request key is global to REFUND, including requests against different payments.
            jdbc.query("select pg_advisory_xact_lock(?)", rs -> {},
                    request.clientRequestId().getMostSignificantBits() ^ request.clientRequestId().getLeastSignificantBits());
            var previous = jdbc.queryForList("select * from payment.payment_refunds where client_request_id=?", request.clientRequestId());
            if (!previous.isEmpty()) {
                var row = previous.get(0);
                if (!actorId.equals(row.get("actor_id"))) throw new RefundException(403, "REFUND_REQUEST_FORBIDDEN");
                if (!hash.equals(row.get("request_hash"))) throw new RefundException(409, "IDEMPOTENCY_CONFLICT");
                return new Accepted(receipt(read((UUID)row.get("id"))), true);
            }
            var payment = payment(paymentId, true);
            if (!"SUCCESS".equals(payment.get("status")) || !"VNPAY".equals(payment.get("method")))
                throw new RefundException(422, "PAYMENT_NOT_REFUNDABLE");
            if (payment.get("paid_at") == null || payment.get("transaction_code") == null
                    || !payment.get("transaction_code").toString().matches("[0-9]{1,15}"))
                throw new RefundException(422, "PAYMENT_PROVIDER_REFERENCE_MISSING");
            if (hasPending(paymentId)) throw new RefundException(422, "REFUND_RECONCILIATION_PENDING");
            BigDecimal total = (BigDecimal)payment.get("amount");
            BigDecimal remaining = total.subtract(sum(paymentId, "status='SUCCEEDED'"));
            if (request.amount().compareTo(remaining) > 0) throw new RefundException(422, "REFUND_EXCEEDS_REMAINING");
            UUID id = UUID.randomUUID();
            String type = request.amount().compareTo(total) == 0 ? "02" : "03";
            jdbc.update("""
                    insert into payment.payment_refunds
                    (id,payment_id,client_request_id,request_hash,actor_id,amount,reason,status,
                     transaction_type,original_transaction_no,original_created_at)
                    values (?,?,?,?,?,?,?,'QUEUED',?,?,?)
                    """, id,paymentId,request.clientRequestId(),hash,actorId,request.amount(),request.reason(),
                    type,payment.get("transaction_code"),payment.get("created_at"));
            return new Accepted(receipt(read(id)), false);
        });
    }

    public Summary summary(long paymentId, int page, int size) {
        if (page < 0 || page > 100000 || size < 1 || size > 100) throw new RefundException(400, "INVALID_PAGE");
        return tx.execute(s -> {
            var p = payment(paymentId, true);
            BigDecimal paid = (BigDecimal)p.get("amount");
            BigDecimal refunded = sum(paymentId, "status='SUCCEEDED'");
            BigDecimal pending = sum(paymentId, "status not in ('SUCCEEDED','REJECTED')");
            String blocked = null;
            if (!"SUCCESS".equals(p.get("status")) || !"VNPAY".equals(p.get("method"))) blocked = "PAYMENT_NOT_REFUNDABLE";
            else if (p.get("paid_at") == null || p.get("transaction_code") == null
                    || !p.get("transaction_code").toString().matches("[0-9]{1,15}")) blocked = "PAYMENT_PROVIDER_REFERENCE_MISSING";
            else if (pending.signum() > 0) blocked = "REFUND_RECONCILIATION_PENDING";
            else if (paid.compareTo(refunded) <= 0) blocked = "PAYMENT_FULLY_REFUNDED";
            var rows = jdbc.query("select * from payment.payment_refunds where payment_id=? order by created_at desc,id limit ? offset ?",
                    this::map, paymentId,size,(long)page*size);
            return new Summary(paymentId,paid,refunded,pending,paid.subtract(refunded).subtract(pending).max(BigDecimal.ZERO),
                    blocked == null,blocked,rows,page,size);
        });
    }

    public Refund read(UUID id) {
        return jdbc.query("select * from payment.payment_refunds where id=?",this::map,id).stream()
                .findFirst().orElseThrow(() -> new RefundException(404,"REFUND_NOT_FOUND"));
    }

    public Optional<Refund> claim(UUID id) {
        return tx.execute(s -> {
            int updated = jdbc.update("update payment.payment_refunds set status='SENDING',attempted_at=now(),updated_at=now() where id=? and status='QUEUED'",id);
            return updated == 1 ? Optional.of(read(id)) : Optional.empty();
        });
    }

    public void complete(UUID id, Outcome outcome) {
        tx.executeWithoutResult(s -> {
            Refund r = read(id);
            var p = payment(r.paymentId(),true);
            r = jdbc.queryForObject("select * from payment.payment_refunds where id=? for update",this::map,id);
            if (Set.of("SUCCEEDED","REJECTED").contains(r.status())) return;
            if (!Set.of("SUCCEEDED","REJECTED","PROCESSING","UNKNOWN").contains(outcome.status()))
                throw new IllegalArgumentException("Invalid provider outcome");
            jdbc.update("update payment.payment_refunds set status=?,provider_code=?,provider_transaction_no=coalesce(?,provider_transaction_no),updated_at=now() where id=?",
                    outcome.status(),outcome.code(),outcome.transactionNo(),id);
            if ("SUCCEEDED".equals(outcome.status()) && sum(r.paymentId(),"status='SUCCEEDED'").compareTo((BigDecimal)p.get("amount")) == 0)
                jdbc.update("update payment.payments set status='REFUNDED',updated_at=now() where id=? and status='SUCCESS'",r.paymentId());
        });
    }

    public List<UUID> queued() {
        // A crash after dispatch may have reached the bank. Never resend these requests.
        jdbc.update("update payment.payment_refunds set status='UNKNOWN',provider_code='DISPATCH_INTERRUPTED',updated_at=now() where status='SENDING' and attempted_at < now()-interval '2 minutes'");
        return jdbc.queryForList("select id from payment.payment_refunds where status='QUEUED' order by created_at limit 10",UUID.class);
    }

    public List<UUID> awaitingReconciliation() {
        return jdbc.queryForList("select id from payment.payment_refunds where status in ('UNKNOWN','PROCESSING') and updated_at < now()-interval '1 minute' order by updated_at limit 10",UUID.class);
    }

    private Map<String,Object> payment(long id, boolean lock) {
        return jdbc.queryForList("select * from payment.payments where id=?"+(lock?" for update":""),id).stream()
                .findFirst().orElseThrow(() -> new RefundException(404,"PAYMENT_NOT_FOUND"));
    }
    private boolean hasPending(long id) { return sum(id,"status not in ('SUCCEEDED','REJECTED')").signum()>0; }
    private BigDecimal sum(long id,String filter) {
        return jdbc.queryForObject("select coalesce(sum(amount),0) from payment.payment_refunds where payment_id=? and "+filter,BigDecimal.class,id);
    }
    private Receipt receipt(Refund r) { return new Receipt(r.id(),r.paymentId(),r.amount(),"ACCEPTED",r.createdAt()); }
    private Refund map(ResultSet rs,int row) throws SQLException {
        return new Refund(rs.getObject("id",UUID.class),rs.getLong("payment_id"),rs.getObject("actor_id",UUID.class),rs.getBigDecimal("amount"),
                rs.getString("reason"),rs.getString("status"),rs.getString("transaction_type"),rs.getString("original_transaction_no"),
                rs.getTimestamp("original_created_at").toInstant(),rs.getString("provider_transaction_no"),rs.getString("provider_code"),
                rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant());
    }
    private void validate(Request r) {
        if (r == null || r.clientRequestId()==null || r.clientRequestId().version()!=4 || r.clientRequestId().variant()!=2
                || r.amount()==null || r.amount().signum()<=0 || r.amount().stripTrailingZeros().scale()>2
                || r.amount().compareTo(new BigDecimal("9999999999.99"))>0
                || r.reason()==null || r.reason().isBlank() || r.reason().length()>1000)
            throw new RefundException(400,"INVALID_REFUND_REQUEST");
    }
    private String hash(long paymentId,Request request) {
        try {
            var payload = new TreeMap<String,Object>();
            payload.put("amount",request.amount().stripTrailingZeros());
            payload.put("paymentId",paymentId);
            payload.put("reason",request.reason());
            byte[] json = new ObjectMapper().writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(json));
        } catch (Exception e) { throw new IllegalStateException("Cannot hash refund request",e); }
    }
}
