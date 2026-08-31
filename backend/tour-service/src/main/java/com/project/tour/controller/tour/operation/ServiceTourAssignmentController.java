package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.service.tour.operation.assignment.ServiceTourAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/service-tour-assignment")
public class ServiceTourAssignmentController {

    private final ServiceTourAssignmentService assignmentService;

    public ServiceTourAssignmentController(
            ServiceTourAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * POST
     * /api/operation/service-tour-assignment
     *
     * Operation phân công khu vực cho Service.
     */
    @PostMapping
    public ResponseEntity<ServiceTourAssignmentResponse> assign(
            @Valid @RequestBody ServiceTourAssignmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assignmentService.assign(request));
    }

    /**
     * GET
     * /api/operation/service-tour-assignment/tour/{tourId}
     *
     * Lấy danh sách Service Assignment của Tour.
     */
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ServiceTourAssignmentResponse>> getByTour(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                assignmentService.getByTour(tourId));
    }

    /**
     * DELETE
     * /api/operation/service-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
     *
     * Xóa Service Assignment.
     */
    @DeleteMapping("/tour/{tourId}/area/{cruiseAreaId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tourId,
            @PathVariable UUID cruiseAreaId) {

        assignmentService.deleteAssignment(
                tourId,
                cruiseAreaId);

        return ResponseEntity.noContent().build();
    }
}