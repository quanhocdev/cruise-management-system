package com.project.activityvisit.mapper;

import com.project.activityvisit.dto.TourVisitSyncResponse;
import com.project.activityvisit.model.TourOfAcitvityVisit;

import java.util.List;

public final class TourVisitSyncMapper {

    private TourVisitSyncMapper() {
    }

    public static TourVisitSyncResponse toResponse(TourOfAcitvityVisit tour) {
        if (tour == null) {
            return null;
        }

        List<TourVisitSyncResponse.ScheduleResponse> schedules = tour.getSchedules() == null ? List.of()
                : tour.getSchedules().stream()
                        .map(schedule -> new TourVisitSyncResponse.ScheduleResponse(
                                schedule.getId(),
                                schedule.getName(),
                                schedule.getDescription(),
                                schedule.getDayNumber(),
                                schedule.getRealDay(),
                                schedule.getStatus(),
                                schedule.getStops() == null ? List.of()
                                        : schedule.getStops().stream()
                                                .map(stop -> new TourVisitSyncResponse.ScheduleStopResponse(
                                                        stop.getId(),
                                                        stop.getPortId(),
                                                        stop.getPortName(),
                                                        stop.getStopOrder(),
                                                        stop.getArriveAt(),
                                                        stop.getLeaveAt()))
                                                .toList()))
                        .toList();

        return new TourVisitSyncResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatusTrip(),
                schedules);
    }
}