
package com.project.payment.kafka;

import com.project.common.event.RefundResultEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RefundResultEventProducer {

    private static final String TOPIC = "refund-result-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RefundResultEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(RefundResultEvent event) {

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
                                event.paymentClientRequestId().toString(),
                                event);
                    }
                });
    }
}
