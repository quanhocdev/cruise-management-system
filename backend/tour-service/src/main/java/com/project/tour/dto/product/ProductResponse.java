package com.project.tour.dto.product;

import com.project.tour.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        String imagePublicId,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt) {
}