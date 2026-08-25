package com.project.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourConfiguredEvent(
        UUID productTourId,
        UUID tourId,
        UUID cruiseAreaId,
        UUID productId,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String imageUrl,
        String status,
        LocalDateTime configuredAt) {
}