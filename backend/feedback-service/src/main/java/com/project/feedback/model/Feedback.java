package com.project.feedback.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "feedbacks", uniqueConstraints = @UniqueConstraint(
    name = "uk_feedback_booking_reviewer", columnNames = {"booking_id", "reviewer_user_id"}), indexes = {
    @Index(name = "idx_feedback_tour_status", columnList = "tour_id,status"),
    @Index(name = "idx_feedback_cruise_status", columnList = "cruise_id,status")
})
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "booking_id", nullable = false) private Long bookingId;
    @Column(name = "passenger_voyage_id", nullable = false) private Long passengerVoyageId;
    @Column(name = "reviewer_user_id", nullable = false) private Long reviewerUserId;
    @Column(name = "tour_id", nullable = false) private UUID tourId;
    @Column(name = "cruise_id", nullable = false) private UUID cruiseId;
    @Column(nullable = false) private Integer rating;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "feedback_images", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "image_url", nullable = false, length = 1000)
    @OrderColumn(name = "display_order") private List<String> imageUrls = new ArrayList<>();
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private FeedbackStatus status;
    @Column(name = "moderation_reason", length = 1000) private String moderationReason;
    @Column(name = "moderated_by_user_id") private Long moderatedByUserId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;
    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getBookingId() { return bookingId; } public void setBookingId(Long v) { bookingId = v; }
    public Long getPassengerVoyageId() { return passengerVoyageId; } public void setPassengerVoyageId(Long v) { passengerVoyageId = v; }
    public Long getReviewerUserId() { return reviewerUserId; } public void setReviewerUserId(Long v) { reviewerUserId = v; }
    public UUID getTourId() { return tourId; } public void setTourId(UUID v) { tourId = v; }
    public UUID getCruiseId() { return cruiseId; } public void setCruiseId(UUID v) { cruiseId = v; }
    public Integer getRating() { return rating; } public void setRating(Integer v) { rating = v; }
    public String getContent() { return content; } public void setContent(String v) { content = v; }
    public List<String> getImageUrls() { return imageUrls; } public void setImageUrls(List<String> v) { imageUrls = v; }
    public FeedbackStatus getStatus() { return status; } public void setStatus(FeedbackStatus v) { status = v; }
    public String getModerationReason() { return moderationReason; } public void setModerationReason(String v) { moderationReason = v; }
    public Long getModeratedByUserId() { return moderatedByUserId; } public void setModeratedByUserId(Long v) { moderatedByUserId = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant v) { updatedAt = v; }
}
