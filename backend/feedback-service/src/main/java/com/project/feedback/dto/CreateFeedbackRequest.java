package com.project.feedback.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;
import com.project.feedback.model.*;

public record CreateFeedbackRequest(
    @NotNull @Positive Long bookingId,
    FeedbackType feedbackType,
    FeedbackTargetType targetType,
    UUID targetId,
    @NotNull @Min(1) @Max(5) Integer rating,
    @NotBlank @Size(max = 5000) String content,
    @Size(max = 5) List<@NotBlank @Size(max = 1000) String> imageUrls
) {}
