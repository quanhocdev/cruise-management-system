
package com.project.feedback.repository;

import com.project.feedback.model.FeedbackInvitation;
import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.model.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackInvitationRepository
        extends JpaRepository<FeedbackInvitation, UUID> {

    Optional<FeedbackInvitation> findByBookingIdAndFeedbackTypeAndTargetId(
            Long bookingId,
            FeedbackType feedbackType,
            UUID targetId);

    List<FeedbackInvitation> findByReviewerUserIdOrderByCreatedAtDesc(
            UUID reviewerUserId);

    List<FeedbackInvitation> findByReviewerUserIdAndStatusOrderByCreatedAtDesc(
            UUID reviewerUserId,
            FeedbackInvitationStatus status);

    Optional<FeedbackInvitation> findByIdAndReviewerUserId(
            UUID id,
            UUID reviewerUserId);

    boolean existsByBookingIdAndFeedbackTypeAndTargetId(
            Long bookingId,
            FeedbackType feedbackType,
            UUID targetId);
}
