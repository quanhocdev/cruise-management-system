package com.project.tour.service.tour.operation;

import com.project.common.event.VisitTourConfiguredEvent;
import com.project.tour.dto.tour.operation.AssignmentActivityVisitResponse;
import com.project.tour.mapper.tour.operation.AssignmentActivityVisitMapper;
import com.project.tour.model.AssignmentActivityVisit;
import com.project.tour.repository.tour.AssignmentActivityVisitRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationActivityVisitTourService {

    private final AssignmentActivityVisitRepository assignmentRepository;

    public OperationActivityVisitTourService(
            AssignmentActivityVisitRepository assignmentRepository) {

        this.assignmentRepository = assignmentRepository;
    }

    // =========================================================
    // KAFKA - VISIT TOUR CONFIGURED
    // =========================================================

    public void handleVisitTourConfigured(
            VisitTourConfiguredEvent event) {

        AssignmentActivityVisit assignment = assignmentRepository
                .findByTourIdAndScheduleStopId(
                        event.tourId(),
                        event.scheduleStopId())
                .orElseGet(() -> new AssignmentActivityVisit(
                        event.tourId(),
                        event.scheduleStopId()));

        assignment.setVisitTourId(event.visitTourId());
        assignment.setVisitName(event.name());
        assignment.setVisitDescription(event.description());
        assignment.setStartTime(event.startTime());
        assignment.setEndTime(event.endTime());
        assignment.setMaxPassengers(event.maxPassengers());
        assignment.setPrice(event.price());
        assignment.setStatus(event.status());

        assignmentRepository.save(assignment);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public List<AssignmentActivityVisitResponse> getAll() {

        return assignmentRepository
                .findAllByOrderByCreatedAtAsc()
                .stream()
                .map(AssignmentActivityVisitMapper::toResponse)
                .toList();
    }

    // =========================================================
    // GET BY TOUR
    // =========================================================

    @Transactional(readOnly = true)
    public List<AssignmentActivityVisitResponse> getByTourId(
            UUID tourId) {

        return assignmentRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(AssignmentActivityVisitMapper::toResponse)
                .toList();
    }
}