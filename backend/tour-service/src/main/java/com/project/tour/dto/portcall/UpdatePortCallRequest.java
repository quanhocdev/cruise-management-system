package com.project.tour.dto.portcall;

import com.project.tour.model.enums.PortCallStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatePortCallRequest(
    @NotNull(message = "Port is required") UUID portId,
    @NotNull(message = "Planned arrival time is required") LocalDateTime plannedArrivalTime,
    @NotNull(message = "Planned departure time is required") LocalDateTime plannedDepartureTime,
    LocalDateTime actualArrivalTime,
    LocalDateTime actualDepartureTime,
    LocalDateTime returnDeadline,
    @NotNull(message = "Port call status is required") PortCallStatus status
) {}
