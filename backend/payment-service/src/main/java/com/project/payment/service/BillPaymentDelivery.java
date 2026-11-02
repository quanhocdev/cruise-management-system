
package com.project.payment.service;

import com.project.common.event.BillPaymentSuccessEvent;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class BillPaymentDelivery {

    private static final String TOPIC = "bill-payment-success-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BillPaymentDelivery(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void deliver(Payment payment) {

        if (payment == null || payment.getId() == null) {
            throw new IllegalArgumentException("Payment is invalid");
        }

        if (payment.getReferenceType() != PaymentReferenceType.BILL) {
            throw new IllegalArgumentException(
                    "Only BILL payments can be delivered");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException(
                    "Payment must be SUCCESS before delivery");
        }

        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "BillPaymentDelivery must be called inside an active transaction");
        }

        BillPaymentSuccessEvent event = new BillPaymentSuccessEvent(
                payment.getReferenceId(),
                payment.getId(),
                payment.getPayerId(),
                payment.getStatus().name(),
                payment.getPaidAt());

        String key = payment.getReferenceId().toString();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        publish(event, key);
                    }
                });
    }

    private void publish(
            BillPaymentSuccessEvent event,
            String key) {

        try {
            kafkaTemplate.send(TOPIC, key, event)
                    .get(10, TimeUnit.SECONDS);

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Bill payment event delivery interrupted",
                    ex);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Cannot publish bill payment success event",
                    ex);
        }
    }
}
