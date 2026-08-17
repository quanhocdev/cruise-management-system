package com.project.feedback.dto;

import java.time.Instant;
import java.util.*;

public record PublicFeedbackResponse(Long id, UUID tourId, UUID cruiseId, Integer rating,
    String content, List<String> imageUrls, Instant createdAt, Instant updatedAt) {}
