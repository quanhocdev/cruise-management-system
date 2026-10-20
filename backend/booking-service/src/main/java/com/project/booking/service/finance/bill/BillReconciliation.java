package com.project.booking.service.finance.bill;

import com.project.booking.client.PaymentClient;
import com.project.common.event.BillPaymentSuccessEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@DependsOn("registrationSchemaInitializer")
public class BillReconciliation {
    private final JdbcTemplate jdbc;
    private final PaymentClient payments;
    private final BillPaymentResultService results;
    private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(BillReconciliation.class);
    public BillReconciliation(JdbcTemplate jdbc,PaymentClient payments,BillPaymentResultService results) {
        this.jdbc=jdbc; this.payments=payments; this.results=results;
    }
    @Scheduled(fixedDelayString="${bill.reconcile-delay-ms:30000}",initialDelayString="${bill.reconcile-delay-ms:30000}")
    public void reconcile() {
        for(Long id:jdbc.queryForList("SELECT id FROM booking.bills WHERE status='PENDING_PAYMENT' ORDER BY updated_at LIMIT 50",Long.class)) {
            try {
                for(var payment:payments.getBillPayments(id)) {
                    if("SUCCESS".equals(payment.status())) {
                        results.processPaymentSuccess(new BillPaymentSuccessEvent(id,payment.paymentId(),payment.payerId(),"SUCCESS",null));
                        break;
                    }
                }
            } catch(Exception e) { log.warn("Bill {} verification deferred ({})",id,e.getClass().getSimpleName()); }
        }
    }
}
