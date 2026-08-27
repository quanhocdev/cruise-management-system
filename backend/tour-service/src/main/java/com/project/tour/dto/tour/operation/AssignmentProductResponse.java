package com.project.tour.dto.tour.operation;

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