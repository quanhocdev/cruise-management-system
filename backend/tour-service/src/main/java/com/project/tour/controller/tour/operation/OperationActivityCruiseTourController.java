package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityCruiseResponse;
import com.project.tour.service.tour.operation.OperationActivityCruiseTourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/activity-cruise-tours")
public class OperationActivityCruiseTourController {

    private final OperationActivityCruiseTourService operationService;

    public OperationActivityCruiseTourController(
            OperationActivityCruiseTourService operationService) {

        this.operationService = operationService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<AssignmentActivityCruiseResponse>> getAll() {

        return ResponseEntity.ok(
                operationService.getAll());
    }

    // =========================================================
    // GET BY TOUR
    // =========================================================

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<AssignmentActivityCruiseResponse>> getByTourId(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                operationService.getByTourId(tourId));
    }
}