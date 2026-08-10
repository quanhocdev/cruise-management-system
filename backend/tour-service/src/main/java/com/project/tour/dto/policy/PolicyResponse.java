package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import java.time.LocalDateTime;
import java.util.UUID;

public record PolicyResponse(
    UUID id, PolicyType type, String title, String content,
    PolicyStatus status, LocalDateTime createdAt, LocalDateTime updatedAt
) {}
