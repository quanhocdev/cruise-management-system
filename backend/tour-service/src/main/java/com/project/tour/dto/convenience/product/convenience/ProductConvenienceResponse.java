package com.project.tour.dto.convenience.product.convenience;

import com.project.tour.model.convenience.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductConvenienceResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        ProductStatus status) {
}