package com.project.tour.dto.tour.convenience.product;

import com.project.tour.model.enums.convenience.ProductTourStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourResponse(

        UUID id,

        // Tour
        UUID tourId,
        String tourCode,
        String tourName,

        // Product
        UUID productId,
        String productName,
        String productDescription,
        String productImageUrl,

        // Cruise Area
        UUID cruiseAreaId,
        String cruiseAreaName,

        // Cruise Deck
        UUID cruiseDeckId,
        Integer deckNumber,

        // Configuration
        Integer quantity,

        ProductTourStatus status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}