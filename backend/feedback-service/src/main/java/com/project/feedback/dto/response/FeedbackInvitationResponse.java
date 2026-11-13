
package com.project.feedback.dto.response;

import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.model.enums.FeedbackType;

import java.time.Instant;
import java.util.UUID;

public record FeedbackInvitationResponse(

        UUID id,
        Long bookingId,
        Long bookingPassengerId,
        FeedbackType feedbackType,
        UUID targetId,
        String targetName,
        FeedbackInvitationStatus status,
        Instant createdAt

) {
}
