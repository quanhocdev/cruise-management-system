package com.project.payment.service;

import com.project.payment.dto.*;
import com.project.payment.mapper.BillPaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.*;
import com.project.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {
    private final PaymentRepository payments;
    private final PaymentProvider provider;
    private final BillPaymentMapper mapper;
    private final JdbcTemplate jdbc;
    private final long timeout;
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    public BillPaymentServiceImpl(PaymentRepository payments, PaymentProvider provider, BillPaymentMapper mapper,
            JdbcTemplate jdbc, @Value("${vnpay.payment-timeout-minutes:15}") long timeout) {
        this.payments=payments; this.provider=provider; this.mapper=mapper; this.jdbc=jdbc; this.timeout=timeout;
    }
    @Override @Transactional
    public BillPaymentResponse createBillPayment(BillPaymentRequest request) { return command(request).getBody(); }

    @Transactional
    public ResponseEntity<BillPaymentResponse> command(BillPaymentRequest r) {
        if (r.billId()==null || r.payerId()==null || r.amount()==null || r.amount().signum()<=0)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"INVALID_BILL_PAYMENT");
        UUID key=r.clientRequestId();
        if(key!=null) {
            if(key.version()!=4 || key.variant()!=2) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_CLIENT_REQUEST_ID");
            String hash=hash(r);
            int inserted=jdbc.update("INSERT INTO payment.bill_payment_requests(client_request_id,payload_hash) VALUES (?,?) ON CONFLICT DO NOTHING",key,hash);
            var stored=jdbc.queryForMap("SELECT payload_hash,response_body FROM payment.bill_payment_requests WHERE client_request_id=? FOR UPDATE",key);
            if(!hash.equals(stored.get("payload_hash"))) throw new ResponseStatusException(HttpStatus.CONFLICT,"IDEMPOTENCY_CONFLICT");
            if(inserted==0) {
                try { return ResponseEntity.status(201).header("Idempotent-Replay","true").body(json.readValue((String)stored.get("response_body"),BillPaymentResponse.class)); }
                catch(Exception e) { throw new IllegalStateException("Cannot restore bill payment",e); }
            }
        }
        jdbc.update("INSERT INTO payment.bill_payment_locks(bill_id) VALUES (?) ON CONFLICT DO NOTHING",r.billId());
        jdbc.queryForObject("SELECT bill_id FROM payment.bill_payment_locks WHERE bill_id=? FOR UPDATE",Long.class,r.billId());
        var attempts=payments.findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(r.billId(),PaymentReferenceType.BILL);
        for(var p:attempts) {
            if(!r.payerId().equals(p.getPayerId()) || r.amount().compareTo(p.getAmount())!=0)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"BILL_PAYMENT_MISMATCH");
        }
        Payment payment=attempts.stream().filter(p -> p.getStatus()==PaymentStatus.SUCCESS || p.getStatus()==PaymentStatus.REFUNDED).findFirst().orElse(null);
        if(payment==null) payment=attempts.stream().filter(p -> p.getStatus()==PaymentStatus.PENDING || p.getStatus()==PaymentStatus.EXPIRED).findFirst().orElse(null);
        if(payment==null) {
            payment=new Payment(); payment.setReferenceId(r.billId()); payment.setReferenceType(PaymentReferenceType.BILL);
            payment.setPayerId(r.payerId()); payment.setAmount(r.amount()); payment.setMethod(PaymentMethod.VNPAY);
            payment.setStatus(PaymentStatus.PENDING); payment.setCreatedAt(Instant.now()); payment.setUpdatedAt(Instant.now());
            payment.setExpiresAt(Instant.now().plus(timeout,ChronoUnit.MINUTES));
            // VNPay's transaction reference must use the persisted payment ID.
            payments.saveAndFlush(payment);
            payment.setPaymentUrl(provider.createPaymentUrl(payment,"127.0.0.1"));
            payments.save(payment);
        }
        // Expiry alone does not prove failure: preserve unknown attempts for reconciliation.
        BillPaymentResponse result=mapper.toResponse(payment);
        if(key!=null) {
            try { jdbc.update("UPDATE payment.bill_payment_requests SET response_body=? WHERE client_request_id=?",json.writeValueAsString(result),key); }
            catch(Exception e) { throw new IllegalStateException("Cannot store bill payment",e); }
        }
        return ResponseEntity.status(201).body(result);
    }
    private String hash(BillPaymentRequest r) {
        try {
            String canonical=json.writeValueAsString(new TreeMap<>(Map.of("amount",r.amount().stripTrailingZeros(),"billId",r.billId(),"payerId",r.payerId())));
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(canonical.getBytes(StandardCharsets.UTF_8)));
        } catch(Exception e) { throw new IllegalStateException(e); }
    }
}
