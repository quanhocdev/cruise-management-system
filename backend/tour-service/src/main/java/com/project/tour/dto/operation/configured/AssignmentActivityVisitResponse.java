package com.project.tour.dto.operation.configured;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentActivityVisitResponse(

                UUID id,

                UUID tourId,

                UUID scheduleStopId,

                UUID visitTourId,

                String visitName,

                String visitDescription,

                LocalDateTime startTime,

                LocalDateTime endTime,

                Integer maxPassengers,

                BigDecimal price,

                String status,

                LocalDateTime createdAt,

                LocalDateTime updatedAt

) {
}