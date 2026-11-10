package com.project.booking.kafka;

import com.project.common.event.ActivityCruiseCompletedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ActivityCruiseCompletedEventProducer {

    private static final String TOPIC = "activity-cruise-completed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ActivityCruiseCompletedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(ActivityCruiseCompletedEvent event) {

        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "Activity cruise completion event must be sent inside an active transaction");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(
                                TOPIC,
                                event.activityCruiseTourId().toString(),
                                event);
                    }
                });
    }
}