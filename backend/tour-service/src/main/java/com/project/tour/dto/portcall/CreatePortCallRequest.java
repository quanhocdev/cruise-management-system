package com.project.tour.dto.portcall;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePortCallRequest(
    @NotNull(message = "Port is required") UUID portId,
    @NotNull(message = "Planned arrival time is required") LocalDateTime plannedArrivalTime,
    @NotNull(message = "Planned departure time is required") LocalDateTime plannedDepartureTime,
    LocalDateTime returnDeadline
) {}
