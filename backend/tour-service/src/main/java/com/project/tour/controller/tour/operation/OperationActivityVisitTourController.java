package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentActivityVisitResponse;
import com.project.tour.service.tour.operation.OperationActivityVisitTourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/activity-visit-tours")
public class OperationActivityVisitTourController {

    private final OperationActivityVisitTourService operationService;

    public OperationActivityVisitTourController(
            OperationActivityVisitTourService operationService) {

        this.operationService = operationService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<AssignmentActivityVisitResponse>> getAll() {

        return ResponseEntity.ok(
                operationService.getAll());
    }

    // =========================================================
    // GET BY TOUR
    // =========================================================

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<AssignmentActivityVisitResponse>> getByTourId(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                operationService.getByTourId(tourId));
    }
}