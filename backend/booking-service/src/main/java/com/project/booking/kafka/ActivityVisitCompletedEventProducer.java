package com.project.booking.kafka;

import com.project.common.event.ActivityVisitCompletedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ActivityVisitCompletedEventProducer {

    private static final String TOPIC = "activity-visit-completed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ActivityVisitCompletedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(ActivityVisitCompletedEvent event) {

        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "Activity visit completion event must be sent inside an active transaction");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(
                                TOPIC,
                                event.visitTourId().toString(),
                                event);
                    }
                });
    }
}