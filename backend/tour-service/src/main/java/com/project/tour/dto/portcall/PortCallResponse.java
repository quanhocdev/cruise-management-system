package com.project.tour.dto.portcall;

import com.project.tour.model.enums.PortCallStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record PortCallResponse(
    UUID id, UUID itineraryDayId, UUID portId, String portName,
    LocalDateTime plannedArrivalTime, LocalDateTime actualArrivalTime,
    LocalDateTime plannedDepartureTime, LocalDateTime actualDepartureTime,
    LocalDateTime returnDeadline, PortCallStatus status
) {}
