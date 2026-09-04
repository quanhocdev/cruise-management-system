package com.project.activityvisit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TourVisitSyncResponse(
        UUID id,
        String code,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String statusTrip,
        List<ScheduleResponse> schedules) {
    public record ScheduleResponse(
            UUID id,
            String name,
            String description,
            Integer dayNumber,
            LocalDate realDay,
            String status,
            List<ScheduleStopResponse> stops) {
    }

    public record ScheduleStopResponse(
            UUID id,
            UUID portId,
            String portName,
            Integer stopOrder,
            LocalDateTime arriveAt,
            LocalDateTime leaveAt) {
    }
}