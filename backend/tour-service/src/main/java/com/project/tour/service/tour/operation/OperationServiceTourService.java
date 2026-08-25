package com.project.tour.service.tour.operation;

import com.project.common.event.ServiceTourConfiguredEvent;
import com.project.tour.dto.tour.operation.AssignmentServiceResponse;
import com.project.tour.mapper.tour.operation.AssignmentServiceMapper;
import com.project.tour.model.AssignmentService;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationServiceTourService {

    private final AssignmentServiceRepository assignmentServiceRepository;

    public OperationServiceTourService(
            AssignmentServiceRepository assignmentServiceRepository) {

        this.assignmentServiceRepository = assignmentServiceRepository;
    }

    // =========================================================
    // KAFKA - SERVICE TOUR CONFIGURED
    // =========================================================

    public void handleServiceTourConfigured(
            ServiceTourConfiguredEvent event) {

        AssignmentService assignment = assignmentServiceRepository
                .findByTourIdAndCruiseAreaId(
                        event.tourId(),
                        event.cruiseAreaId())
                .orElseThrow(() -> new IllegalStateException(
                        "AssignmentService not found for tourId="
                                + event.tourId()
                                + ", cruiseAreaId="
                                + event.cruiseAreaId()));

        assignment.setServiceTourId(event.serviceTourId());
        assignment.setServiceId(event.serviceId());
        assignment.setServiceName(event.name());
        assignment.setServiceDescription(event.description());
        assignment.setPrice(event.price());
        assignment.setMaxPassengers(event.maxPassengers());
        assignment.setDurationMinutes(event.durationMinutes());
        assignment.setImageUrl(event.imageUrl());
        assignment.setStatus(event.status());

        assignmentServiceRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentServiceResponse> getAll() {

        return assignmentServiceRepository
                .findAllByOrderByCreatedAtAsc()
                .stream()
                .map(AssignmentServiceMapper::toResponse)
                .toList();
    }

    // =========================================================
    // GET SERVICE ASSIGNMENTS BY TOUR
    // =========================================================

    @Transactional(readOnly = true)
    public List<AssignmentServiceResponse> getByTourId(
            UUID tourId) {

        return assignmentServiceRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .map(AssignmentServiceMapper::toResponse)
                .toList();
    }
}