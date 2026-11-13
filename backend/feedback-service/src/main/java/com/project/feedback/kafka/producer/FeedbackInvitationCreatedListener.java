package com.project.feedback.kafka.producer;

import com.project.common.event.FeedbackInvitationCreatedEvent;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FeedbackInvitationCreatedListener {

    private final FeedbackInvitationEventProducer eventProducer;

    public FeedbackInvitationCreatedListener(
            FeedbackInvitationEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvitationCreated(
            FeedbackInvitationCreatedEvent event) {

        eventProducer.publishInvitationCreated(event);
    }
}