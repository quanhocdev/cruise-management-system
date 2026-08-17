package com.project.feedback.dto;

import com.project.feedback.model.FeedbackStatus;
import java.time.Instant;
import java.util.*;

public record FeedbackResponse(Long id, Long bookingId, Long passengerVoyageId, Long reviewerUserId,
    UUID tourId, UUID cruiseId, Integer rating, String content, List<String> imageUrls,
    FeedbackStatus status, String moderationReason, Instant createdAt, Instant updatedAt) {}
