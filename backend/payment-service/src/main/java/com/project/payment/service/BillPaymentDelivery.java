package com.project.payment.service;

import com.project.common.event.BillPaymentSuccessEvent;
import com.project.payment.model.Payment;
import com.project.payment.repository.BillPaymentDeliveryRepository;
import com.project.payment.repository.PaymentRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@DependsOn("paymentRefundSchemaInitializer")
public class BillPaymentDelivery {

    private final BillPaymentDeliveryRepository deliveryRepository;
    private final PaymentRepository payments;
    private final KafkaTemplate<String, Object> kafka;

    public BillPaymentDelivery(
            BillPaymentDeliveryRepository deliveryRepository,
            PaymentRepository payments,
            KafkaTemplate<String, Object> kafka) {

        this.deliveryRepository = deliveryRepository;
        this.payments = payments;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelayString = "${bill-payment.delivery-delay-ms:15000}")
    @Transactional
    public void deliver() {

        List<Long> ids = deliveryRepository.findPendingPaymentIds(20);

        for (Long id : ids) {

            Payment payment = payments.findById(id)
                    .orElseThrow(() -> new IllegalStateException(
                            "Payment not found: " + id));

            try {
                BillPaymentSuccessEvent event = new BillPaymentSuccessEvent(
                        payment.getReferenceId(),
                        payment.getId(),
                        payment.getPayerId(),
                        payment.getStatus().name(),
                        payment.getPaidAt());

                kafka.send(
                        "bill-payment-success-topic",
                        payment.getReferenceId().toString(),
                        event).get(10, TimeUnit.SECONDS);

                deliveryRepository.markAsDelivered(id);

            } catch (InterruptedException ex) {

                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Bill payment delivery interrupted. Will retry.",
                        ex);

            } catch (Exception ex) {

                throw new IllegalStateException(
                        "Bill payment delivery failed. Will retry.",
                        ex);
            }
        }
    }
}