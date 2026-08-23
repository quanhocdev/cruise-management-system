package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentRequest;
import com.project.tour.service.tour.operation.ActivityCruiseTourAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Operation phân công khu vực Activity cho Tour.
     * Validate tại tour-service -> Bắn Event Kafka xử lý bất đồng bộ.
     */
    @PostMapping
    public ResponseEntity<Void> assign(
            @Valid @RequestBody ActivityCruiseTourAssignmentRequest request) {

        assignmentService.assign(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * Xóa phân công Activity theo tourId và cruiseAreaId.
     * Bắn Event Kafka với action "DELETE".
     */
    @DeleteMapping("/tour/{tourId}/area/{cruiseAreaId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable UUID tourId,
            @PathVariable UUID cruiseAreaId) {

        assignmentService.deleteAssignment(tourId, cruiseAreaId);
        return ResponseEntity.noContent().build();
    }
}