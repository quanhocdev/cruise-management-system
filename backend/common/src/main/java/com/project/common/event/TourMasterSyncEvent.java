package com.project.common.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TourMasterSyncEvent(
        UUID tourId,
        String code,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String statusTrip,

        CruiseDetail cruise,
        List<ScheduleDetail> schedules,
        List<TourAssignmentEvent> assignments,
        LocalDateTime timestamp) {
    public record CruiseDetail(
            UUID cruiseId,
            String code,
            String name,
            String description,
            Integer maxPassengers,
            String imageUrl,
            String status,
            List<DeckDetail> decks) {
    }

    public record DeckDetail(
            UUID deckId,
            Integer deckNumber,
            String status,
            List<AreaDetail> areas) {
    }

    public record AreaDetail(
            UUID areaId,
            String name,
            String description,
            String status,
            String imageUrl) {
    }

    public record ScheduleDetail(
            UUID scheduleId,
            String name,
            String description,
            Integer dayNumber,
            LocalDate realDay,
            String status,
            List<ScheduleStopDetail> stops) {
    }

    public record ScheduleStopDetail(
            UUID scheduleStopId,
            UUID portId,
            String portName,
            Integer stopOrder,
            LocalDateTime arriveAt,
            LocalDateTime leaveAt) {
    }

    public TourMasterSyncEvent(
            UUID tourId,
            String code,
            String name,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String statusTrip,
            CruiseDetail cruise,
            List<ScheduleDetail> schedules,
            List<TourAssignmentEvent> assignments) {
        this(tourId, code, name, description, startDate, endDate, statusTrip,
                cruise, schedules, assignments, LocalDateTime.now());
    }
}