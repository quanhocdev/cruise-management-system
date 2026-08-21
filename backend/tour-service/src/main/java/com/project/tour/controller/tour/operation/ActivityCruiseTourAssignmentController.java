package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;
import com.project.tour.service.tour.operation.ActivityCruiseTourAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/activity-cruise-tour-assignment")
public class ActivityCruiseTourAssignmentController {

    private final ActivityCruiseTourAssignmentService assignmentService;

    public ActivityCruiseTourAssignmentController(
            ActivityCruiseTourAssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    /**
     * Operation phân công khu vực cho Tour.
     */
    @PostMapping
    public ResponseEntity<ActivityCruiseTourAssignmentResponse> assign(
            @Valid @RequestBody ActivityCruiseTourAssignmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assignmentService.assign(request));
    }

    /**
     * Lấy danh sách phân công của một Tour.
     */
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ActivityCruiseTourAssignmentResponse>> getByTour(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                assignmentService.getByTour(tourId));
    }

    /**
     * Xóa phân công khi vẫn đang WAITING_CONFIG.
     */
    @DeleteMapping("/tour/{tourId}/area/{cruiseAreaId}")
    public ResponseEntity<Void> deleteByTourAndArea(
            @PathVariable UUID tourId,
            @PathVariable UUID cruiseAreaId) {

        assignmentService.deleteByTourAndArea(tourId, cruiseAreaId);

        return ResponseEntity.noContent().build();
    }
}