package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.service.tour.operation.ServiceTourAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/tours")
public class ServiceTourAssignmentController {

    private final ServiceTourAssignmentService assignmentService;

    public ServiceTourAssignmentController(
            ServiceTourAssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    /**
     * Operation phân công khu vực cho Service.
     */
    @PostMapping("/{tourId}/service-assignments")
    public ResponseEntity<ServiceTourAssignmentResponse> assign(
            @PathVariable UUID tourId,
            @Valid @RequestBody ServiceTourAssignmentRequest request) {

        /*
         * Đảm bảo tourId trên URL và request body thống nhất.
         */
        if (!tourId.equals(request.tourId())) {
            return ResponseEntity.badRequest().build();
        }

        ServiceTourAssignmentResponse response = assignmentService.assign(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Lấy danh sách Service Assignment của Tour.
     */
    @GetMapping("/{tourId}/service-assignments")
    public ResponseEntity<List<ServiceTourAssignmentResponse>> getByTour(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                assignmentService.getByTour(tourId));
    }

    /**
     * Xóa Service Assignment.
     */
    @DeleteMapping("/{tourId}/service-assignments/{cruiseAreaId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable UUID tourId,
            @PathVariable UUID cruiseAreaId) {

        assignmentService.deleteAssignment(
                tourId,
                cruiseAreaId);

        return ResponseEntity.noContent().build();
    }
}