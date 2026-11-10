package com.project.feedback.model;

import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.model.enums.FeedbackType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "feedback_invitations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_feedback_invitation_booking_type_target", columnNames = { "booking_id",
                "feedback_type", "target_id" })
})
public class FeedbackInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "booking_passenger_id")
    private Long bookingPassengerId;

    @Column(name = "reviewer_user_id")
    private UUID reviewerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 30)
    private FeedbackType feedbackType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FeedbackInvitationStatus status = FeedbackInvitationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = FeedbackInvitationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public FeedbackInvitation() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingPassengerId() {
        return bookingPassengerId;
    }

    public void setBookingPassengerId(Long bookingPassengerId) {
        this.bookingPassengerId = bookingPassengerId;
    }

    public UUID getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(UUID reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public FeedbackInvitationStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackInvitationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}