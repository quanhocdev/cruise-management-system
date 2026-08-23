package com.project.tour.dto.tour.operation;

import com.project.tour.model.enums.convenience.ServiceTourStatus;

import java.time.Instant;
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

                UUID serviceId,
                String serviceName,
                String serviceDescription,

                Integer maxPassengers,
                Integer durationMinutes,

                ServiceTourStatus status,

                Instant createdAt,
                Instant updatedAt

) {
}