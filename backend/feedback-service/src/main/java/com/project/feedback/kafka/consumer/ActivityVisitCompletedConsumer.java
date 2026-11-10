
package com.project.feedback.kafka.consumer;

import com.project.common.event.ActivityVisitCompletedEvent;
import com.project.feedback.service.FeedbackInvitationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityVisitCompletedConsumer {

    private final FeedbackInvitationService feedbackInvitationService;

    public ActivityVisitCompletedConsumer(
            FeedbackInvitationService feedbackInvitationService) {
        this.feedbackInvitationService = feedbackInvitationService;
    }

    @KafkaListener(topics = "activity-visit-completed-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ActivityVisitCompletedEvent event) {

        feedbackInvitationService.createActivityVisitInvitation(event);
    }
}
