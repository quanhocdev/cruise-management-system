package com.project.booking.kafka;

import com.project.common.event.RefundApprovedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RefundApprovedEventProducer {

    private static final String TOPIC = "refund-approved-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RefundApprovedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(RefundApprovedEvent event) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "No active transaction synchronization");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(
                                TOPIC,
                                event.bookingRefundId().toString(),
                                event);
                    }
                });
    }
}