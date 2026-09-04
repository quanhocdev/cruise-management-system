package com.project.convenience.dto.product.convenience;

import com.project.convenience.model.enums.ProductTourStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourResponse(
                UUID id,

                // Tour info (Thuộc Tour Service)
                UUID tourId,
                String tourCode,
                String tourName,

                // Product info (Thuộc Convenience Service)
                UUID productId,
                String productName,
                String productDescription,
                String productImageUrl,

                // Cruise Area info (Thuộc Tour Service)
                UUID cruiseAreaId,
                String cruiseAreaName,

                // Cruise Deck info (Thuộc Tour Service)
                UUID cruiseDeckId,
                Integer deckNumber,

                // Configuration
                Integer quantity,
                ProductTourStatus status,

                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}