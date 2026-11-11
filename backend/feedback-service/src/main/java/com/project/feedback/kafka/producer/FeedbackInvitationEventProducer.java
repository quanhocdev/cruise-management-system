package com.project.feedback.kafka.producer;

import com.project.common.event.FeedbackInvitationCreatedEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedbackInvitationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${feedback.kafka.topic.invitation-created}")
    private String invitationCreatedTopic;

    public FeedbackInvitationEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInvitationCreated(
            FeedbackInvitationCreatedEvent event) {

        kafkaTemplate.send(
                invitationCreatedTopic,
                event.reviewerUserId().toString(),
                event);
    }
}