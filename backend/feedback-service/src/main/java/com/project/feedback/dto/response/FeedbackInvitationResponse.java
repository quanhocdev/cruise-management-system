
package com.project.feedback.dto.response;

import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.model.enums.FeedbackType;

import java.time.Instant;
import java.util.UUID;

public record FeedbackInvitationResponse(

        UUID id,
        Long bookingId,
        FeedbackType feedbackType,
        UUID targetId,
        FeedbackInvitationStatus status,
        Instant createdAt

) {
}
