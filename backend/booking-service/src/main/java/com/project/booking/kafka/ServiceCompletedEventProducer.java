
package com.project.booking.kafka;

import com.project.common.event.ServiceCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ServiceCompletedEventProducer {

    private static final String TOPIC = "service-completed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ServiceCompletedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(ServiceCompletedEvent event) {

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
                                event.serviceTourId().toString(),
                                event);
                    }
                });
    }
}
