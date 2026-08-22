package com.project.convenience.dto.product;

import com.project.convenience.model.enums.ProductStatus;

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