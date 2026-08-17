package com.project.tour.dto.onboard;

import com.project.tour.model.enums.onboard.ActivityCruiseStatus;
import java.time.LocalDateTime;
import java.util.UUID;

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