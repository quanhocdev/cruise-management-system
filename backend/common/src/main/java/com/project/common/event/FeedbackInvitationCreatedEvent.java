package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record FeedbackInvitationCreatedEvent(
        UUID invitationId,
        UUID reviewerUserId,
        Long bookingId,
        String feedbackType,
        UUID targetId,
        String targetName,
        Instant createdAt) {
}