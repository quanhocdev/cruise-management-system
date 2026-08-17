package com.project.tour.service.tour.schedule;

import com.project.tour.dto.tour.schedule.CreateScheduleRequest;
import com.project.tour.dto.tour.schedule.ScheduleResponse;
import com.project.tour.dto.tour.schedule.UpdateScheduleRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.ScheduleMapper;
import com.project.tour.model.Schedule;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.model.enums.tour.TourStatusTrip;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScheduleService {

        private final ScheduleRepository scheduleRepository;
        private final TourRepository tourRepository;

        public ScheduleService(
                        ScheduleRepository scheduleRepository,
                        TourRepository tourRepository) {

                this.scheduleRepository = scheduleRepository;
                this.tourRepository = tourRepository;
        }

        private void validateTourIsDraft(Tour tour) {

                if (tour.getStatusTrip() != TourStatusTrip.DRAFT) {
                        throw new AppException(
                                        "Tour must be in DRAFT status to modify schedule",
                                        HttpStatus.BAD_REQUEST);
                }
        }

        public ScheduleResponse create(
                        UUID tourId,
                        CreateScheduleRequest request) {

                Tour tour = findTour(tourId);

                validateTourIsDraft(tour);

                if (scheduleRepository.existsByTour_IdAndDayNumber(
                                tourId,
                                request.getDayNumber())) {

                        throw new AppException(
                                        "Day number already exists in this tour",
                                        HttpStatus.CONFLICT);
                }

                Schedule schedule = ScheduleMapper.toEntity(
                                request,
                                tour);

                Schedule saved = scheduleRepository.save(schedule);

                return ScheduleMapper.toResponse(saved);
        }

        @Transactional(readOnly = true)
        public ScheduleResponse getById(
                        UUID tourId,
                        UUID scheduleId) {

                Schedule schedule = findById(
                                tourId,
                                scheduleId);

                return ScheduleMapper.toResponse(schedule);
        }

        @Transactional(readOnly = true)
        public List<ScheduleResponse> getAll(
                        UUID tourId) {

                findTour(tourId);

                return scheduleRepository
                                .findAllByTour_IdOrderByDayNumberAsc(tourId)
                                .stream()
                                .map(ScheduleMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ScheduleResponse> getActive(
                        UUID tourId) {

                findTour(tourId);

                return scheduleRepository
                                .findAllByTour_IdAndStatusOrderByDayNumberAsc(
                                                tourId,
                                                ScheduleStatus.ACTIVE)
                                .stream()
                                .map(ScheduleMapper::toResponse)
                                .toList();
        }

        public ScheduleResponse update(
                        UUID tourId,
                        UUID scheduleId,
                        UpdateScheduleRequest request) {

                Schedule schedule = findById(
                                tourId,
                                scheduleId);
                validateTourIsDraft(schedule.getTour());

                if (scheduleRepository
                                .existsByTour_IdAndDayNumberAndIdNot(
                                                tourId,
                                                request.getDayNumber(),
                                                scheduleId)) {

                        throw new AppException(
                                        "Day number already exists in this tour",
                                        HttpStatus.CONFLICT);
                }

                ScheduleMapper.updateEntity(
                                schedule,
                                request);

                Schedule updated = scheduleRepository.save(schedule);

                return ScheduleMapper.toResponse(updated);
        }

        public void delete(
                        UUID tourId,
                        UUID scheduleId) {

                Schedule schedule = findById(
                                tourId,
                                scheduleId);

                validateTourIsDraft(schedule.getTour());

                scheduleRepository.delete(schedule);
        }

        private Tour findTour(UUID tourId) {

                return tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));
        }

        private Schedule findById(
                        UUID tourId,
                        UUID scheduleId) {

                return scheduleRepository
                                .findByIdAndTour_Id(
                                                scheduleId,
                                                tourId)
                                .orElseThrow(() -> new AppException(
                                                "Schedule not found",
                                                HttpStatus.NOT_FOUND));
        }
}