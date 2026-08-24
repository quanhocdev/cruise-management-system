package com.project.activityvisit.mapper;

import com.project.activityvisit.dto.ShoreTourConfigurationResponse;
import com.project.activityvisit.model.VisitTour;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ShoreTourConfigurationMapper {

        private ShoreTourConfigurationMapper() {
        }

        // =====================================================
        // VISIT TOURS -> SHORE TOUR CONFIGURATION RESPONSE
        // =====================================================

        public static ShoreTourConfigurationResponse toResponse(
                        List<VisitTour> visitTours) {

                if (visitTours == null || visitTours.isEmpty()) {
                        return null;
                }

                VisitTour first = visitTours.get(0);

                // =====================================================
                // GROUP BY SCHEDULE
                // =====================================================

                Map<UUID, List<VisitTour>> visitToursBySchedule = visitTours.stream()
                                .collect(Collectors.groupingBy(
                                                VisitTour::getScheduleId));

                // =====================================================
                // BUILD SCHEDULE CONFIGURATIONS
                // =====================================================

                List<ShoreTourConfigurationResponse.ScheduleConfiguration> schedules = visitToursBySchedule.values()
                                .stream()
                                .map(ShoreTourConfigurationMapper::toScheduleConfiguration)
                                .toList();

                return new ShoreTourConfigurationResponse(
                                first.getTourId(),
                                first.getTourCode(),
                                first.getTourName(),
                                null, // tourDescription chưa có trong VisitTour

                                null, // startDate chưa có trong VisitTour
                                null, // endDate chưa có trong VisitTour

                                schedules);
        }

        // =====================================================
        // SCHEDULE
        // =====================================================

        private static ShoreTourConfigurationResponse.ScheduleConfiguration toScheduleConfiguration(
                        List<VisitTour> visitTours) {

                VisitTour first = visitTours.get(0);

                // =====================================================
                // GROUP BY SCHEDULE STOP
                // =====================================================

                Map<UUID, List<VisitTour>> visitToursByStop = visitTours.stream()
                                .collect(Collectors.groupingBy(
                                                VisitTour::getScheduleStopId));

                List<ShoreTourConfigurationResponse.ScheduleStopConfiguration> stops = visitToursByStop.values()
                                .stream()
                                .map(ShoreTourConfigurationMapper::toScheduleStopConfiguration)
                                .toList();

                return new ShoreTourConfigurationResponse.ScheduleConfiguration(
                                first.getScheduleId(),
                                first.getDayNumber(),
                                null, // realDay chưa có trong VisitTour
                                null, // scheduleName chưa có trong VisitTour
                                stops);
        }

        // =====================================================
        // SCHEDULE STOP
        // =====================================================

        private static ShoreTourConfigurationResponse.ScheduleStopConfiguration toScheduleStopConfiguration(
                        List<VisitTour> visitTours) {

                VisitTour first = visitTours.get(0);

                List<ShoreTourConfigurationResponse.VisitTourConfiguration> visitTourConfigurations = visitTours
                                .stream()
                                .map(ShoreTourConfigurationMapper::toVisitTourConfiguration)
                                .toList();

                return new ShoreTourConfigurationResponse.ScheduleStopConfiguration(

                                first.getScheduleStopId(),

                                first.getPortId(),
                                first.getPortName(),

                                first.getStopOrder(),

                                first.getArriveAt(),
                                first.getLeaveAt(),

                                visitTourConfigurations);
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
}