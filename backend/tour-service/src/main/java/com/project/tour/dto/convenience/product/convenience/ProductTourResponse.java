package com.project.tour.dto.convenience.product.convenience;

import com.project.tour.model.convenience.enums.ProductTourStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourResponse(
        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        UUID productId,
        String productName,
        String productDescription,
        String productImageUrl,

        UUID cruiseAreaId,
        String cruiseAreaName,

        UUID cruiseDeckId,
        Integer deckNumber,

        Integer quantity,

        ProductTourStatus status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}