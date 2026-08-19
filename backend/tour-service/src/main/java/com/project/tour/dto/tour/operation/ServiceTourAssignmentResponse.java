package com.project.tour.dto.tour.operation;

import com.project.tour.model.enums.convenience.ServiceTourStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceTourAssignmentResponse(

        UUID id,

        UUID tourId,

        UUID cruiseAreaId,

        UUID serviceId,

        Integer maxPassengers,

        Integer durationMinutes,

        ServiceTourStatus status,

        Instant createdAt,

        Instant updatedAt

) {
}