package com.project.feedback.dto;

import com.project.feedback.model.FeedbackStatus;
import jakarta.validation.constraints.*;

public record ModerateFeedbackRequest(
    @NotNull FeedbackStatus status,
    @Size(max = 1000) String reason
) {}
