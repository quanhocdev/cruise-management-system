package com.project.tour.dto.tour.operation;

import com.project.tour.model.enums.convenience.ProductTourStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourAssignmentResponse(

        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        UUID productId,
        String productName,

        UUID cruiseAreaId,
        String cruiseAreaName,

        UUID cruiseDeckId,
        Integer deckNumber,

        Integer quantity,

        ProductTourStatus status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}