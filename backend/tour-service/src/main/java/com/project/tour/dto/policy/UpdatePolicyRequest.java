package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePolicyRequest(
    @NotNull(message = "Policy type is required") PolicyType type,
    @NotBlank(message = "Policy title is required")
    @Size(max = 200, message = "Policy title must not exceed 200 characters") String title,
    @NotBlank(message = "Policy content is required") String content,
    @NotNull(message = "Policy status is required") PolicyStatus status
) {}
