package com.project.tour.dto.tour.operation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentServiceResponse(
        UUID id,
        UUID tourId,
        UUID cruiseAreaId,
        UUID serviceTourId,
        UUID serviceId,
        String serviceName,
        String serviceDescription,
        BigDecimal price,
        Integer maxPassengers,
        Integer durationMinutes,
        String imageUrl,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}