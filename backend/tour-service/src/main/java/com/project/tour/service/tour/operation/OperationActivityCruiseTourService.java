package com.project.tour.service.tour.operation;

import com.project.common.event.ActivityCruiseTourConfiguredEvent;
import com.project.tour.dto.tour.operation.AssignmentActivityCruiseResponse;
import com.project.tour.mapper.tour.operation.AssignmentActivityCruiseMapper;
import com.project.tour.model.AssignmentActivityCruise;
import com.project.tour.repository.tour.AssignmentActivityCruiseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationActivityCruiseTourService {

        private final AssignmentActivityCruiseRepository assignmentRepository;

        public OperationActivityCruiseTourService(
                        AssignmentActivityCruiseRepository assignmentRepository) {

                this.assignmentRepository = assignmentRepository;
        }

        // =========================================================
        // KAFKA - ACTIVITY CRUISE TOUR CONFIGURED
        // =========================================================

        public void handleActivityCruiseTourConfigured(
                        ActivityCruiseTourConfiguredEvent event) {

                AssignmentActivityCruise assignment = assignmentRepository
                                .findByTourIdAndCruiseAreaId(
                                                event.tourId(),
                                                event.cruiseAreaId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "AssignmentActivityCruise not found for tourId="
                                                                + event.tourId()
                                                                + ", cruiseAreaId="
                                                                + event.cruiseAreaId()));

                assignment.setActivityCruiseTourId(
                                event.activityCruiseTourId());

                assignment.setActivityCruiseId(
                                event.activityCruiseId());

                assignment.setActivityName(
                                event.name());

                assignment.setActivityDescription(
                                event.description());

                assignment.setStartTime(
                                event.startTime());

                assignment.setEndTime(
                                event.endTime());

                assignment.setMaxPassengers(
                                event.maxPassengers());

                assignment.setPrice(
                                event.price());

                assignment.setImageUrl(
                                event.imageUrl());

                assignment.setStatus(
                                event.status());

                assignmentRepository.save(assignment);
        }

        // =========================================================
        // GET ALL
        // =========================================================

        @Transactional(readOnly = true)
        public List<AssignmentActivityCruiseResponse> getAll() {

                return assignmentRepository
                                .findAllByOrderByCreatedAtAsc()
                                .stream()
                                .map(AssignmentActivityCruiseMapper::toResponse)
                                .toList();
        }

        // =========================================================
        // GET BY TOUR
        // =========================================================

        @Transactional(readOnly = true)
        public List<AssignmentActivityCruiseResponse> getByTourId(
                        UUID tourId) {

                return assignmentRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(AssignmentActivityCruiseMapper::toResponse)
                                .toList();
        }
}