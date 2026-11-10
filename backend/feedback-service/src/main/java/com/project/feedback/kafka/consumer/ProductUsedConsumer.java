
package com.project.feedback.kafka.consumer;

import com.project.common.event.ProductUsedEvent;
import com.project.feedback.service.FeedbackInvitationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductUsedConsumer {

    private final FeedbackInvitationService feedbackInvitationService;

    public ProductUsedConsumer(
            FeedbackInvitationService feedbackInvitationService) {
        this.feedbackInvitationService = feedbackInvitationService;
    }

    @KafkaListener(topics = "product-used-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ProductUsedEvent event) {

        feedbackInvitationService.createProductInvitation(event);
    }
}
