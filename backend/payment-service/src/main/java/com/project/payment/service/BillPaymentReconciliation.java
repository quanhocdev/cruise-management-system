
package com.project.payment.service;

import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;
import com.project.payment.repository.BillPaymentReconciliationRepository;
import com.project.payment.service.refund.VnPayRefundProvider;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class BillPaymentReconciliation {

    private final BillPaymentReconciliationRepository reconciliationRepository;
    private final PaymentRepository payments;
    private final VnPayRefundProvider provider;
    private final TransactionTemplate transaction;
    private final BillPaymentDelivery billPaymentDelivery;

    public BillPaymentReconciliation(
            BillPaymentReconciliationRepository reconciliationRepository,
            PaymentRepository payments,
            VnPayRefundProvider provider,
            PlatformTransactionManager manager,
            BillPaymentDelivery billPaymentDelivery) {

        this.reconciliationRepository = reconciliationRepository;
        this.payments = payments;
        this.provider = provider;
        this.transaction = new TransactionTemplate(manager);
        this.billPaymentDelivery = billPaymentDelivery;
    }

    public void reconcile(Long paymentId) {

        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Invalid payment ID");
        }

        Payment payment = payments.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING
                && payment.getStatus() != PaymentStatus.EXPIRED) {
            return;
        }

        // Query VNPay outside the database transaction.
        var result = provider.queryPayment(payment);

        if (!"SUCCEEDED".equals(result.status())
                && !"REJECTED".equals(result.status())) {
            return;
        }

        transaction.executeWithoutResult(status -> {

            if (payment.getReferenceType() == PaymentReferenceType.BILL) {
                reconciliationRepository.createBillLock(
                        payment.getReferenceId());

                reconciliationRepository.lockBill(
                        payment.getReferenceId());
            }

            Payment current = payments.findForUpdate(paymentId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Payment not found: " + paymentId));

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

            // Notify booking-service when a BILL payment is confirmed.
            if (current.getReferenceType() == PaymentReferenceType.BILL
                    && current.getStatus() == PaymentStatus.SUCCESS) {
                billPaymentDelivery.deliver(current);
            }
        });
    }
}
