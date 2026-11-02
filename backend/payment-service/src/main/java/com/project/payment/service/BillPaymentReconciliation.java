package com.project.payment.service;

import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.BillPaymentReconciliationRepository;
import com.project.payment.repository.PaymentRepository;
import com.project.payment.service.refund.VnPayRefundProvider;

import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

@Component
@DependsOn("paymentRefundSchemaInitializer")
public class BillPaymentReconciliation {

    private final BillPaymentReconciliationRepository reconciliationRepository;
    private final PaymentRepository payments;
    private final VnPayRefundProvider provider;
    private final TransactionTemplate transaction;

    public BillPaymentReconciliation(
            BillPaymentReconciliationRepository reconciliationRepository,
            PaymentRepository payments,
            VnPayRefundProvider provider,
            PlatformTransactionManager manager) {

        this.reconciliationRepository = reconciliationRepository;
        this.payments = payments;
        this.provider = provider;
        this.transaction = new TransactionTemplate(manager);
    }

    @Scheduled(fixedDelayString = "${bill-payment.query-delay-ms:300000}", initialDelayString = "${bill-payment.query-delay-ms:300000}")
    public void reconcile() {

        List<Long> ids = reconciliationRepository.findDuePaymentIds();

        for (Long id : ids) {

            int claimed = reconciliationRepository.claimPayment(id);

            if (claimed == 0) {
                continue;
            }

            Payment payment = payments.findById(id)
                    .orElseThrow(() -> new IllegalStateException(
                            "Payment not found: " + id));

            var result = provider.queryPayment(payment);

            if (!"SUCCEEDED".equals(result.status())
                    && !"REJECTED".equals(result.status())) {
                continue;
            }

            transaction.executeWithoutResult(status -> {

                reconciliationRepository.createBillLock(
                        payment.getReferenceId());

                reconciliationRepository.lockBill(
                        payment.getReferenceId());

                Payment current = payments.findForUpdate(id)
                        .orElseThrow(() -> new IllegalStateException(
                                "Payment not found: " + id));

                if (current.getStatus() != PaymentStatus.PENDING
                        && current.getStatus() != PaymentStatus.EXPIRED) {
                    return;
                }

                current.setStatus(
                        "SUCCEEDED".equals(result.status())
                                ? PaymentStatus.SUCCESS
                                : PaymentStatus.FAILED);

                if (current.getStatus() == PaymentStatus.SUCCESS) {
                    current.setPaidAt(Instant.now());
                }

                current.setTransactionCode(result.transactionNo());
                current.setResponseCode(result.code());
                current.setUpdatedAt(Instant.now());

                payments.save(current);
            });
        }
    }
}