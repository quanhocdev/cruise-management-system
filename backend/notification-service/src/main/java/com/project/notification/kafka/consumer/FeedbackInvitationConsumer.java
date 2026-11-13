
package com.project.notification.kafka.consumer;

import com.project.common.event.FeedbackInvitationCreatedEvent;
import com.project.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FeedbackInvitationConsumer {

    private final NotificationService notificationService;

    public FeedbackInvitationConsumer(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${notification.kafka.topic.feedback-invitation-created}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeFeedbackInvitationCreated(
            FeedbackInvitationCreatedEvent event) {
        notificationService.createFeedbackInvitationNotification(event);
    }
}
