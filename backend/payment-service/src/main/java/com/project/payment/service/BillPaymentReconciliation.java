package com.project.payment.service;

import com.project.payment.refund.VnPayRefundProvider;
import com.project.payment.repository.PaymentRepository;
import com.project.payment.model.enums.PaymentStatus;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;

@Component
@DependsOn("paymentRefundSchemaInitializer")
public class BillPaymentReconciliation {
    private final JdbcTemplate jdbc;
    private final PaymentRepository payments;
    private final VnPayRefundProvider provider;
    private final TransactionTemplate transaction;
    public BillPaymentReconciliation(JdbcTemplate jdbc,PaymentRepository payments,VnPayRefundProvider provider,PlatformTransactionManager manager){
        this.jdbc=jdbc;this.payments=payments;this.provider=provider;transaction=new TransactionTemplate(manager);
    }
    @Scheduled(fixedDelayString="${bill-payment.query-delay-ms:300000}",initialDelayString="${bill-payment.query-delay-ms:300000}")
    public void reconcile(){
        var ids=jdbc.queryForList("""
            SELECT p.id FROM payment.payments p LEFT JOIN payment.bill_query_schedule q ON q.payment_id=p.id
            WHERE p.reference_type='BILL' AND p.status IN ('PENDING','EXPIRED') AND p.expires_at<CURRENT_TIMESTAMP
            AND (q.next_attempt_at IS NULL OR q.next_attempt_at<=CURRENT_TIMESTAMP) ORDER BY p.id LIMIT 10
            """,Long.class);
        for(Long id:ids){
            int claimed=jdbc.update("""
                INSERT INTO payment.bill_query_schedule(payment_id,next_attempt_at) VALUES (?,CURRENT_TIMESTAMP+interval '5 minutes')
                ON CONFLICT(payment_id) DO UPDATE SET next_attempt_at=EXCLUDED.next_attempt_at
                WHERE bill_query_schedule.next_attempt_at<=CURRENT_TIMESTAMP
                """,id);
            if(claimed==0)continue;
            var payment=payments.findById(id).orElseThrow();
            var result=provider.queryPayment(payment);
            if(!"SUCCEEDED".equals(result.status()) && !"REJECTED".equals(result.status()))continue;
            transaction.executeWithoutResult(s->{
                jdbc.update("INSERT INTO payment.bill_payment_locks(bill_id) VALUES (?) ON CONFLICT DO NOTHING",payment.getReferenceId());
                jdbc.queryForObject("SELECT bill_id FROM payment.bill_payment_locks WHERE bill_id=? FOR UPDATE",Long.class,payment.getReferenceId());
                var current=payments.findForUpdate(id).orElseThrow();
                if(current.getStatus()!=PaymentStatus.PENDING && current.getStatus()!=PaymentStatus.EXPIRED)return;
                current.setStatus("SUCCEEDED".equals(result.status())?PaymentStatus.SUCCESS:PaymentStatus.FAILED);
                if(current.getStatus()==PaymentStatus.SUCCESS)current.setPaidAt(Instant.now());
                current.setTransactionCode(result.transactionNo());current.setResponseCode(result.code());current.setUpdatedAt(Instant.now());
                payments.save(current);
            });
        }
    }
}
