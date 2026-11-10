
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

    // Tìm invitation của đúng lượt sử dụng
    Optional<FeedbackInvitation> findByFeedbackTypeAndSourceUsageId(
            FeedbackType feedbackType,
            Long sourceUsageId);

    // Danh sách invitation của người đại diện booking
    List<FeedbackInvitation> findByReviewerUserIdOrderByCreatedAtDesc(
            UUID reviewerUserId);

    List<FeedbackInvitation> findByReviewerUserIdAndStatusOrderByCreatedAtDesc(
            UUID reviewerUserId,
            FeedbackInvitationStatus status);

    Optional<FeedbackInvitation> findByIdAndReviewerUserId(
            UUID id,
            UUID reviewerUserId);

    // Chống Kafka redelivery tạo invitation trùng
    boolean existsByFeedbackTypeAndSourceUsageId(
            FeedbackType feedbackType,
            Long sourceUsageId);
}
