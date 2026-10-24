package com.project.booking.kafka;

import com.project.common.event.ProductUsedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ProductUsedEventProducer {

    private static final String TOPIC = "product-used-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductUsedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ProductUsedEvent event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { publish(event); }
            });
        } else {
            publish(event);
        }
    }

    private void publish(ProductUsedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.productTourId().toString(),
                event);
    }
}
