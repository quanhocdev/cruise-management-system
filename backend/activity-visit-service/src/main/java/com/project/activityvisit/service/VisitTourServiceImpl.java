package com.project.activityvisit.service;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.mapper.VisitTourMapper;
import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.repository.VisitTourRepository;
import com.project.activityvisit.exception.AppException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourServiceImpl implements VisitTourService {

        private final VisitTourRepository visitTourRepository;
        private final VisitTourValidator validator;

        public VisitTourServiceImpl(
                        VisitTourRepository visitTourRepository,
                        VisitTourValidator validator) {

                this.visitTourRepository = visitTourRepository;
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
        public VisitTourResponse getById(UUID id) {

                VisitTour visitTour = findById(id);

                return VisitTourMapper.toResponse(visitTour);
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
                                .findAllByTourIdOrderByStartTimeAsc(tourId)
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

                VisitTour visitTour = VisitTourMapper.toEntity(request);

                visitTour.setScheduleStopId(scheduleStopId);

                validator.validateTimeRange(
                                visitTour.getArriveAt(),
                                visitTour.getLeaveAt(),
                                request.startTime(),
                                request.endTime());

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

                validator.validateUpdate(request);

                VisitTourMapper.updateEntity(
                                visitTour,
                                request);

                validator.validateTimeRange(
                                visitTour.getArriveAt(),
                                visitTour.getLeaveAt(),
                                visitTour.getStartTime(),
                                visitTour.getEndTime());

                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
        }

        // =====================================================
        // DELETE
        // =====================================================

        @Override
        @Transactional
        public void delete(UUID id) {

                VisitTour visitTour = findById(id);

                visitTourRepository.delete(visitTour);
        }

        // =====================================================
        // FINDER
        // =====================================================

        private VisitTour findById(UUID id) {

                return visitTourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Visit tour not found",
                                                HttpStatus.NOT_FOUND));
        }
}