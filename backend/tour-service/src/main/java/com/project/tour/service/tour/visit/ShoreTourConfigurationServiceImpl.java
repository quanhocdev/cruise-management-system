package com.project.tour.service.tour.visit;

import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.visit.ShoreTourConfigurationMapper;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.Tour;
import com.project.tour.model.VisitTour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.VisitTourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShoreTourConfigurationServiceImpl
        implements ShoreTourConfigurationService {

    private final TourRepository tourRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final VisitTourRepository visitTourRepository;

    public ShoreTourConfigurationServiceImpl(
            TourRepository tourRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            VisitTourRepository visitTourRepository) {

        this.tourRepository = tourRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.visitTourRepository = visitTourRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ShoreTourConfigurationResponse getConfiguration(
            UUID tourId,
            VisitTourStatus status) {
        // =================================================
        // 1. TOUR
        // =================================================

        List<TourStatusTrip> allowedStatuses = List.of(
                TourStatusTrip.APPROVED,
                TourStatusTrip.READY,
                TourStatusTrip.IN_PROGRESS,
                TourStatusTrip.COMPLETED);

        Tour tour = tourRepository
                .findByIdAndStatusTripIn(
                        tourId,
                        allowedStatuses)
                .orElseThrow(() -> new AppException(
                        "Tour not found or is not available for shore configuration",
                        HttpStatus.NOT_FOUND));

        // =================================================
        // 2. SCHEDULES
        // =================================================

        List<Schedule> schedules = scheduleRepository
                .findAllByTour_IdOrderByDayNumberAsc(
                        tourId);

        if (schedules.isEmpty()) {

            return ShoreTourConfigurationMapper.toResponse(
                    tour,
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyMap());
        }

        List<UUID> scheduleIds = schedules.stream()
                .map(Schedule::getId)
                .toList();

        // =================================================
        // 3. ALL SCHEDULE STOPS
        // =================================================

        List<ScheduleStop> stops = scheduleStopRepository
                .findAllBySchedule_IdInOrderBySchedule_DayNumberAscStopOrderAsc(
                        scheduleIds);

        Map<UUID, List<ScheduleStop>> stopsBySchedule = stops.stream()
                .collect(Collectors.groupingBy(
                        stop -> stop
                                .getSchedule()
                                .getId()));

        if (stops.isEmpty()) {

            return ShoreTourConfigurationMapper.toResponse(
                    tour,
                    schedules,
                    stopsBySchedule,
                    Collections.emptyMap());
        }

        List<UUID> stopIds = stops.stream()
                .map(ScheduleStop::getId)
                .toList();

        // =================================================
        // 4. ALL VISIT TOURS
        // =================================================

        List<VisitTour> visitTours;

        if (status == null) {

            visitTours = visitTourRepository
                    .findAllByScheduleStop_IdInOrderByStartTimeAsc(
                            stopIds);

        } else {

            visitTours = visitTourRepository
                    .findAllByScheduleStop_IdInAndStatusOrderByStartTimeAsc(
                            stopIds,
                            status);
        }

        Map<UUID, List<VisitTour>> visitToursByStop = visitTours.stream()
                .collect(Collectors.groupingBy(
                        visitTour -> visitTour
                                .getScheduleStop()
                                .getId()));

        // =================================================
        // 5. BUILD RESPONSE
        // =================================================

        return ShoreTourConfigurationMapper.toResponse(
                tour,
                schedules,
                stopsBySchedule,
                visitToursByStop);
    }
}