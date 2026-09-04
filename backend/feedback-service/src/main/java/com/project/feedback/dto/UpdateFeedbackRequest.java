package com.project.feedback.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record UpdateFeedbackRequest(
    @NotNull @Min(1) @Max(5) Integer rating,
    @NotBlank @Size(max = 5000) String content,
    @Size(max = 5) List<@NotBlank @Size(max = 1000) String> imageUrls
) {}
