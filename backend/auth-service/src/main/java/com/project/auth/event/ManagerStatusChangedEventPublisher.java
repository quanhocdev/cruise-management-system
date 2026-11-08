
package com.project.auth.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.project.common.event.ManagerStatusChangedEvent;

@Component
public class ManagerStatusChangedEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ManagerStatusChangedEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public ManagerStatusChangedEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.manager-status-changed:manager-status-changed-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleManagerStatusChanged(
            ManagerStatusChangedApplicationEvent event) {

        ManagerStatusChangedEvent kafkaEvent = new ManagerStatusChangedEvent(
                event.userId(),
                event.roleName(),
                event.previousStatus().name(),
                event.newStatus().name(),
                event.changedAt());

        kafkaTemplate.send(
                topic,
                event.userId().toString(),
                kafkaEvent).whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Không thể gửi ManagerStatusChangedEvent. userId={}, role={}",
                                event.userId(),
                                event.roleName(),
                                exception);
                        return;
                    }

                    log.info(
                            "Đã gửi ManagerStatusChangedEvent. userId={}, role={}, {} -> {}",
                            event.userId(),
                            event.roleName(),
                            event.previousStatus(),
                            event.newStatus());
                });
    }
}
