package com.project.payment.service;

import com.project.common.event.BillPaymentSuccessEvent;
import com.project.payment.repository.PaymentRepository;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn("paymentRefundSchemaInitializer")
public class BillPaymentDelivery {
    private final JdbcTemplate jdbc;
    private final PaymentRepository payments;
    private final KafkaTemplate<String,Object> kafka;
    public BillPaymentDelivery(JdbcTemplate jdbc,PaymentRepository payments,KafkaTemplate<String,Object> kafka) {
        this.jdbc=jdbc; this.payments=payments; this.kafka=kafka;
    }
    // Committed SUCCESS rows act as the durable queue; delivery may repeat after a crash.
    @Scheduled(fixedDelayString="${bill-payment.delivery-delay-ms:15000}")
    @Transactional
    public void deliver() {
        var ids=jdbc.queryForList("""
            SELECT p.id FROM payment.payments p
            WHERE p.reference_type='BILL' AND p.status='SUCCESS'
            AND NOT EXISTS(SELECT 1 FROM payment.bill_payment_deliveries d WHERE d.payment_id=p.id)
            ORDER BY p.id LIMIT 20 FOR UPDATE OF p SKIP LOCKED
            """,Long.class);
        for(Long id:ids) {
            var p=payments.findById(id).orElseThrow();
            try {
                kafka.send("bill-payment-success-topic",p.getReferenceId().toString(),
                    new BillPaymentSuccessEvent(p.getReferenceId(),p.getId(),p.getPayerId(),p.getStatus().name(),p.getPaidAt())).get(10,TimeUnit.SECONDS);
                jdbc.update("INSERT INTO payment.bill_payment_deliveries(payment_id) VALUES (?) ON CONFLICT DO NOTHING",id);
            } catch(Exception e) {
                if(e instanceof InterruptedException) Thread.currentThread().interrupt();
                throw new IllegalStateException("Bill payment delivery will be retried",e);
            }
        }
    }
}
