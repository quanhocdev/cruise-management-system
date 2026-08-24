package com.project.activityvisit.service;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.repository.VisitTourRepository;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.VisitTourMapper;
import com.project.tour.model.ScheduleStop;
import com.project.tour.model.VisitTour;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourServiceImpl
                implements VisitTourService {

        private final VisitTourRepository visitTourRepository;
        private final ScheduleStopRepository scheduleStopRepository;
        private final VisitTourValidator validator;

        public VisitTourServiceImpl(
                        VisitTourRepository visitTourRepository,
                        ScheduleStopRepository scheduleStopRepository,
                        VisitTourValidator validator) {

                this.visitTourRepository = visitTourRepository;

                this.scheduleStopRepository = scheduleStopRepository;

                this.validator = validator;
        }

        // =====================================================
        // GET ALL
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getAll() {

                return visitTourRepository
                                .findAllByOrderByCreatedAtDesc()
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET BY ID
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public VisitTourResponse getById(
                        UUID id) {

                VisitTour visitTour = findById(id);

                return VisitTourMapper.toResponse(
                                visitTour);
        }

        // =====================================================
        // GET BY SCHEDULE STOP
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getByScheduleStop(
                        UUID scheduleStopId) {

                return visitTourRepository
                                .findAllByScheduleStopIdOrderByStartTimeAsc(
                                                scheduleStopId)
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET BY TOUR
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getByTour(
                        UUID tourId) {

                return visitTourRepository
                                .findAllByScheduleStopScheduleTourIdOrderByStartTimeAsc(
                                                tourId)
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // CREATE
        // =====================================================

        @Override
        @Transactional
        public VisitTourResponse create(
                        UUID scheduleStopId,
                        CreateVisitTourRequest request) {

                validator.validateCreate(request);

                ScheduleStop scheduleStop = findScheduleStop(
                                scheduleStopId);

                validator.validateTourCanModify(
                                scheduleStop.getSchedule().getTour());

                validator.validateTimeRange(
                                scheduleStop,
                                request.startTime(),
                                request.endTime());

                VisitTour visitTour = VisitTourMapper.toEntity(
                                request,
                                scheduleStop);

                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
        }

        // =====================================================
        // UPDATE - PATCH
        // =====================================================

        @Override
        @Transactional
        public VisitTourResponse update(
                        UUID id,
                        UpdateVisitTourRequest request) {

                VisitTour visitTour = findById(id);

                validator.validateTourCanModify(
                                visitTour.getScheduleStop()
                                                .getSchedule()
                                                .getTour());

                validator.validateUpdate(
                                request);

                /*
                 * ScheduleStop không được thay đổi
                 * bằng PATCH.
                 */

                if (request.name() != null) {
                        visitTour.setName(
                                        request.name().trim());
                }

                if (request.description() != null) {
                        visitTour.setDescription(
                                        request.description());
                }

                if (request.startTime() != null) {
                        visitTour.setStartTime(
                                        request.startTime());
                }

                if (request.endTime() != null) {
                        visitTour.setEndTime(
                                        request.endTime());
                }

                if (request.maxPassengers() != null) {
                        visitTour.setMaxPassengers(
                                        request.maxPassengers());
                }

                if (request.price() != null) {
                        visitTour.setPrice(
                                        request.price());
                }

                if (request.status() != null) {

                        validator.validateStatusTransition(
                                        visitTour.getStatus(),
                                        request.status());

                        visitTour.setStatus(
                                        request.status());
                }

                validator.validateTimeRange(
                                visitTour.getScheduleStop(),
                                visitTour.getStartTime(),
                                visitTour.getEndTime());

                VisitTour saved = visitTourRepository.save(
                                visitTour);

                return VisitTourMapper.toResponse(
                                saved);
        }

        // =====================================================
        // DELETE
        // =====================================================

        @Override
        @Transactional
        public void delete(UUID id) {

                VisitTour visitTour = findById(id);

                validator.validateTourCanModify(
                                visitTour.getScheduleStop()
                                                .getSchedule()
                                                .getTour());

                visitTourRepository.delete(
                                visitTour);
        }

        // =====================================================
        // FINDERS
        // =====================================================

        private VisitTour findById(
                        UUID id) {

                return visitTourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Visit tour not found",
                                                HttpStatus.NOT_FOUND));
        }

        private ScheduleStop findScheduleStop(
                        UUID id) {

                return scheduleStopRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Schedule stop not found",
                                                HttpStatus.NOT_FOUND));
        }
}