package com.project.tour.dto.operation.assignment.service;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceTourAssignmentResponse(

        UUID id,

        UUID tourId,
        String tourCode,
        String tourName,

        UUID cruiseAreaId,
        String cruiseAreaName,

        UUID cruiseDeckId,
        Integer deckNumber,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}