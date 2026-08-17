package com.project.tour.dto.onboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ActivityCruiseResponse(
        Long id,
        Long cruiseAreaId,
        String name,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxPassengers,
        BigDecimal price,
        String status,
        String imageUrl,
        String imagePublicId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}