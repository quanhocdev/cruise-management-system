package com.project.tour.mapper.tour.visit;

import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.Tour;
import com.project.tour.model.VisitTour;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ShoreTourConfigurationMapper {

    private ShoreTourConfigurationMapper() {
    }

    public static ShoreTourConfigurationResponse toResponse(
            Tour tour,
            List<Schedule> schedules,
            Map<UUID, List<ScheduleStop>> stopsBySchedule,
            Map<UUID, List<VisitTour>> visitToursByStop) {

        List<ShoreTourConfigurationResponse.ScheduleConfiguration> scheduleConfigurations = schedules.stream()
                .map(schedule -> toScheduleConfiguration(
                        schedule,
                        stopsBySchedule.getOrDefault(
                                schedule.getId(),
                                Collections.emptyList()),
                        visitToursByStop))
                .toList();

        return new ShoreTourConfigurationResponse(

                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),

                tour.getStartDate(),
                tour.getEndDate(),

                scheduleConfigurations);
    }

    private static ShoreTourConfigurationResponse.ScheduleConfiguration toScheduleConfiguration(
            Schedule schedule,
            List<ScheduleStop> stops,
            Map<UUID, List<VisitTour>> visitToursByStop) {

        List<ShoreTourConfigurationResponse.ScheduleStopConfiguration> stopConfigurations = stops.stream()
                .map(stop -> toStopConfiguration(
                        stop,
                        visitToursByStop.getOrDefault(
                                stop.getId(),
                                Collections.emptyList())))
                .toList();

        return new ShoreTourConfigurationResponse.ScheduleConfiguration(

                schedule.getId(),
                schedule.getDayNumber(),
                schedule.getRealDay(),
                schedule.getName(),

                stopConfigurations);
    }

    private static ShoreTourConfigurationResponse.ScheduleStopConfiguration toStopConfiguration(
            ScheduleStop stop,
            List<VisitTour> visitTours) {

        List<ShoreTourConfigurationResponse.VisitTourConfiguration> visitTourConfigurations = visitTours.stream()
                .map(ShoreTourConfigurationMapper::toVisitTourConfiguration)
                .toList();

        return new ShoreTourConfigurationResponse.ScheduleStopConfiguration(

                stop.getId(),

                stop.getPort().getId(),
                stop.getPort().getName(),

                stop.getStopOrder(),

                stop.getArriveAt(),
                stop.getLeaveAt(),

                visitTourConfigurations);
    }

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