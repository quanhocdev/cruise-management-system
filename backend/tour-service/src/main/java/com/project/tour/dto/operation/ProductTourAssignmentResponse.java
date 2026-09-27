package com.project.tour.dto.operation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductTourAssignmentResponse(

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