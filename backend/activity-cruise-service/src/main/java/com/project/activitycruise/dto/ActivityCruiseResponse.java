package com.project.activitycruise.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.activitycruise.model.enums.ActivityCruiseStatus;

public record ActivityCruiseResponse(
        UUID id,
        String name,
        String description,
        ActivityCruiseStatus status,
        String imageUrl,
        String imagePublicId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}