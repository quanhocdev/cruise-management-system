
package com.project.feedback.mapper;

import com.project.feedback.dto.response.FeedbackInvitationResponse;
import com.project.feedback.dto.response.FeedbackResponse;
import com.project.feedback.model.Feedback;
import com.project.feedback.model.FeedbackInvitation;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public FeedbackInvitationResponse toInvitationResponse(
            FeedbackInvitation invitation) {
        if (invitation == null) {
            return null;
        }

        return new FeedbackInvitationResponse(
                invitation.getId(),
                invitation.getBookingId(),
                invitation.getFeedbackType(),
                invitation.getTargetId(),
                invitation.getStatus(),
                invitation.getCreatedAt());
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        FeedbackInvitation invitation = feedback.getInvitation();

        return new FeedbackResponse(
                feedback.getId(),
                invitation.getId(),
                invitation.getBookingId(),
                invitation.getFeedbackType(),
                invitation.getTargetId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt());
    }
}
