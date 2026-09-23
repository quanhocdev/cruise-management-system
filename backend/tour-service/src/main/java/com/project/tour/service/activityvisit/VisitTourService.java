package com.project.tour.service.activityvisit;

import com.project.tour.dto.activityvisit.CreateVisitTourRequest;
import com.project.tour.dto.activityvisit.UpdateVisitTourRequest;
import com.project.tour.dto.activityvisit.VisitTourResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.activityvisit.VisitTourMapper;
import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.model.activityvisit.enums.VisitTourStatus;
import com.project.tour.repository.activityvisit.VisitTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourService {

        private final VisitTourRepository visitTourRepository;
        private final VisitTourValidator validator;

        public VisitTourService(
                        VisitTourRepository visitTourRepository,
                        VisitTourValidator validator) {

                this.visitTourRepository = visitTourRepository;
                this.validator = validator;
        }

        // =====================================================
        // GET ALL
        // =====================================================

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

        @Transactional(readOnly = true)
        public VisitTourResponse getById(UUID id) {

                return VisitTourMapper.toResponse(
                                findById(id));
        }

        // =====================================================
        // GET BY SCHEDULE STOP
        // =====================================================

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
        // CREATE / CONFIGURE
        // =====================================================

        @Transactional
        public VisitTourResponse create(
                        UUID scheduleStopId,
                        CreateVisitTourRequest request) {

                validator.validateCreate(request);

                VisitTour visitTour = visitTourRepository
                                .findByScheduleStopId(scheduleStopId)
                                .orElseThrow(() -> new AppException(
                                                "Visit tour not found for this schedule stop.",
                                                HttpStatus.NOT_FOUND));

                visitTour.setName(request.name());
                visitTour.setDescription(request.description());
                visitTour.setStartTime(request.startTime());
                visitTour.setEndTime(request.endTime());
                visitTour.setMaxPassengers(request.maxPassengers());
                visitTour.setPrice(request.price());
                visitTour.setStatus(VisitTourStatus.CONFIGURED);

                return VisitTourMapper.toResponse(
                                visitTourRepository.save(visitTour));
        }

        // =====================================================
        // UPDATE
        // =====================================================

        @Transactional
        public VisitTourResponse update(
                        UUID id,
                        UpdateVisitTourRequest request) {

                VisitTour visitTour = findById(id);

                validator.validateUpdate(request);

                VisitTourMapper.updateEntity(
                                visitTour,
                                request);

                return VisitTourMapper.toResponse(
                                visitTourRepository.save(visitTour));
        }

        // =====================================================
        // CREATE FROM TOUR APPROVED
        // =====================================================

        @Transactional
        public VisitTourResponse createVisitTourFromEvent(
                        UUID tourId,
                        UUID scheduleStopId) {

                if (visitTourRepository.existsByTourIdAndScheduleStopId(
                                tourId,
                                scheduleStopId)) {

                        throw new AppException(
                                        "VisitTour already exists for this tour and schedule stop",
                                        HttpStatus.CONFLICT);
                }

                VisitTour visitTour = new VisitTour();

                visitTour.setTourId(tourId);
                visitTour.setScheduleStopId(scheduleStopId);

                return VisitTourMapper.toResponse(
                                visitTourRepository.save(visitTour));
        }

        // =====================================================
        // DELETE
        // =====================================================

        @Transactional
        public void delete(UUID id) {

                visitTourRepository.delete(findById(id));
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