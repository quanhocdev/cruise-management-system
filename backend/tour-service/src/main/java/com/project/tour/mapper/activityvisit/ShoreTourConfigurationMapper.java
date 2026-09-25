package com.project.tour.mapper.activityvisit;

import com.project.tour.dto.activityvisit.ShoreTourConfigurationResponse;
import com.project.tour.model.activityvisit.VisitTour;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ShoreTourConfigurationMapper {

    private ShoreTourConfigurationMapper() {
    }

    // =====================================================
    // BUILD RESPONSE
    // =====================================================

    public static ShoreTourConfigurationResponse toResponse(
            UUID tourId,
            String tourCode,
            String tourName,
            String tourDescription,

            List<ScheduleConfigurationData> schedules) {

        if (schedules == null) {
            schedules = Collections.emptyList();
        }

        return new ShoreTourConfigurationResponse(
                tourId,
                tourCode,
                tourName,
                tourDescription,
                null,
                null,
                schedules.stream()
                        .map(ShoreTourConfigurationMapper::toScheduleConfiguration)
                        .toList());
    }

    // =====================================================
    // SCHEDULE
    // =====================================================

    private static ShoreTourConfigurationResponse.ScheduleConfiguration toScheduleConfiguration(
            ScheduleConfigurationData data) {

        return new ShoreTourConfigurationResponse.ScheduleConfiguration(
                data.scheduleId(),
                data.dayNumber(),
                data.realDay(),
                data.scheduleName(),
                data.stops()
                        .stream()
                        .map(ShoreTourConfigurationMapper::toScheduleStopConfiguration)
                        .toList());
    }

    // =====================================================
    // SCHEDULE STOP
    // =====================================================

    private static ShoreTourConfigurationResponse.ScheduleStopConfiguration toScheduleStopConfiguration(
            ScheduleStopConfigurationData data) {

        return new ShoreTourConfigurationResponse.ScheduleStopConfiguration(

                data.scheduleStopId(),

                data.portId(),
                data.portName(),

                data.stopOrder(),

                data.arriveAt(),
                data.leaveAt(),

                data.visitTours()
                        .stream()
                        .map(ShoreTourConfigurationMapper::toVisitTourConfiguration)
                        .toList());
    }

    // =====================================================
    // VISIT TOUR
    // =====================================================

    private static ShoreTourConfigurationResponse.VisitTourConfiguration toVisitTourConfiguration(
            VisitTour visitTour) {

        return new ShoreTourConfigurationResponse.VisitTourConfiguration(

                visitTour.getId(),

                visitTour.getName(),
                visitTour.getDescription(),

                visitTour.getStartTime(),
                visitTour.getEndTime(),

                visitTour.getMaxPassengers(),

                visitTour.getPrice(),

                visitTour.getStatus());
    }

    // =====================================================
    // INTERNAL DATA
    // =====================================================

    public record ScheduleConfigurationData(

            UUID scheduleId,
            Integer dayNumber,
            LocalDate realDay,
            String scheduleName,
            List<ScheduleStopConfigurationData> stops) {
    }

    public record ScheduleStopConfigurationData(

            UUID scheduleStopId,

            UUID portId,
            String portName,

            Integer stopOrder,

            LocalDateTime arriveAt,
            LocalDateTime leaveAt,

            List<VisitTour> visitTours) {
    }
}
