
package com.project.feedback.kafka.consumer;

import com.project.common.event.ActivityCruiseCompletedEvent;
import com.project.feedback.service.FeedbackInvitationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityCruiseCompletedConsumer {

    private final FeedbackInvitationService feedbackInvitationService;

    public ActivityCruiseCompletedConsumer(
            FeedbackInvitationService feedbackInvitationService) {
        this.feedbackInvitationService = feedbackInvitationService;
    }

    @KafkaListener(topics = "activity-cruise-completed-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ActivityCruiseCompletedEvent event) {

        feedbackInvitationService.createActivityCruiseInvitation(event);
    }
}
