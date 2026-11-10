
package com.project.feedback.dto.response;

import com.project.feedback.model.enums.FeedbackType;

import java.time.Instant;
import java.util.UUID;

public record FeedbackResponse(

        UUID id,
        UUID invitationId,
        Long bookingId,
        FeedbackType feedbackType,
        UUID targetId,
        Integer rating,
        String comment,
        Instant createdAt

) {
}
