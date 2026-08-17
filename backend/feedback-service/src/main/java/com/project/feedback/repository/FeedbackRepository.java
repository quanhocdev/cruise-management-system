package com.project.feedback.repository;

import com.project.feedback.dto.RatingSummary;
import com.project.feedback.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByBookingIdAndReviewerUserId(Long bookingId, Long reviewerUserId);
    List<Feedback> findAllByReviewerUserIdOrderByCreatedAtDesc(Long reviewerUserId);
    List<Feedback> findAllByTourIdAndStatusOrderByCreatedAtDesc(UUID tourId, FeedbackStatus status);
    List<Feedback> findAllByCruiseIdAndStatusOrderByCreatedAtDesc(UUID cruiseId, FeedbackStatus status);
    List<Feedback> findAllByOrderByCreatedAtDesc();
    @Query("select new com.project.feedback.dto.RatingSummary(count(f), coalesce(avg(f.rating), 0.0)) from Feedback f where f.tourId = :id and f.status = com.project.feedback.model.FeedbackStatus.PUBLISHED")
    RatingSummary summarizeTour(@Param("id") UUID id);
    @Query("select new com.project.feedback.dto.RatingSummary(count(f), coalesce(avg(f.rating), 0.0)) from Feedback f where f.cruiseId = :id and f.status = com.project.feedback.model.FeedbackStatus.PUBLISHED")
    RatingSummary summarizeCruise(@Param("id") UUID id);
}
