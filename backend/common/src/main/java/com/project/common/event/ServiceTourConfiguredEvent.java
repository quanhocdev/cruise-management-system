package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceTourConfiguredEvent(
        UUID serviceTourId,
        UUID tourId,
        UUID cruiseAreaId,
        UUID serviceId,
        String name,
        String description,
        BigDecimal price,
        Integer maxPassengers,
        Integer durationMinutes,
        String imageUrl,
        String status,
        LocalDateTime configuredAt) {
}