package com.project.tour.service.activityvisit;

import com.project.tour.dto.activityvisit.TourVisitSyncResponse;
import com.project.tour.exception.AppException;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.Tour;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourMasterService {

        private final TourRepository tourRepository;
        private final ScheduleRepository scheduleRepository;
        private final ScheduleStopRepository scheduleStopRepository;

        public VisitTourMasterService(
                        TourRepository tourRepository,
                        ScheduleRepository scheduleRepository,
                        ScheduleStopRepository scheduleStopRepository) {

                this.tourRepository = tourRepository;
                this.scheduleRepository = scheduleRepository;
                this.scheduleStopRepository = scheduleStopRepository;
        }

        @Transactional(readOnly = true)
        public TourVisitSyncResponse getMasterTourById(UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return toResponse(tour);
        }

        @Transactional(readOnly = true)
        public List<TourVisitSyncResponse> getAllMasterTours() {

                return tourRepository
                                .findAllByOrderByNameAsc()
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private TourVisitSyncResponse toResponse(Tour tour) {

                List<Schedule> schedules = scheduleRepository
                                .findAllByTour_IdOrderByDayNumberAsc(
                                                tour.getId());

                List<TourVisitSyncResponse.ScheduleResponse> scheduleResponses = schedules.stream()
                                .map(this::toScheduleResponse)
                                .toList();

                return new TourVisitSyncResponse(
                                tour.getId(),
                                tour.getCode(),
                                tour.getName(),
                                tour.getDescription(),
                                tour.getStartDate(),
                                tour.getEndDate(),
                                tour.getStatusTrip() == null
                                                ? null
                                                : tour.getStatusTrip().name(),
                                scheduleResponses);
        }

        private TourVisitSyncResponse.ScheduleResponse toScheduleResponse(
                        Schedule schedule) {

                List<ScheduleStop> stops = scheduleStopRepository
                                .findAllBySchedule_IdOrderByStopOrderAsc(
                                                schedule.getId());

                List<TourVisitSyncResponse.ScheduleStopResponse> stopResponses = stops.stream()
                                .map(this::toScheduleStopResponse)
                                .toList();

                return new TourVisitSyncResponse.ScheduleResponse(
                                schedule.getId(),
                                schedule.getName(),
                                schedule.getDescription(),
                                schedule.getDayNumber(),
                                schedule.getRealDay(),
                                schedule.getStatus() == null
                                                ? null
                                                : schedule.getStatus().name(),
                                stopResponses);
        }

        private TourVisitSyncResponse.ScheduleStopResponse toScheduleStopResponse(
                        ScheduleStop stop) {

                UUID portId = null;
                String portName = null;

                if (stop.getPort() != null) {
                        portId = stop.getPort().getId();
                        portName = stop.getPort().getName();
                }

                return new TourVisitSyncResponse.ScheduleStopResponse(
                                stop.getId(),
                                portId,
                                portName,
                                stop.getStopOrder(),
                                stop.getArriveAt(),
                                stop.getLeaveAt());
        }
}