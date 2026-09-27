package com.project.tour.dto.operation.configured;

import java.math.BigDecimal;
import java.util.UUID;

public record AssignmentProductResponse(
                UUID id,
                UUID tourId,
                UUID cruiseAreaId,
                UUID productTourId,
                UUID productId,
                String productName,
                String productDescription,
                BigDecimal price,
                Integer quantity,
                String imageUrl,
                String status) {
}