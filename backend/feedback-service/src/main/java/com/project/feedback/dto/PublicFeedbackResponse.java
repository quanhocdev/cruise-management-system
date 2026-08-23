package com.project.feedback.dto;

import java.time.Instant;
import java.util.*;
import com.project.feedback.model.*;

public record PublicFeedbackResponse(Long id, UUID tourId, UUID cruiseId,
    FeedbackType feedbackType, FeedbackTargetType targetType, UUID targetId, Integer rating,
    String content, List<String> imageUrls, Instant createdAt, Instant updatedAt) {}
