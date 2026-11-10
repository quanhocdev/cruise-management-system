
package com.project.feedback.kafka.consumer;

import com.project.common.event.ServiceCompletedEvent;
import com.project.feedback.service.FeedbackInvitationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ServiceCompletedConsumer {

    private final FeedbackInvitationService feedbackInvitationService;

    public ServiceCompletedConsumer(
            FeedbackInvitationService feedbackInvitationService) {
        this.feedbackInvitationService = feedbackInvitationService;
    }

    @KafkaListener(topics = "service-completed-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ServiceCompletedEvent event) {

        feedbackInvitationService.createServiceInvitation(event);
    }
}
