package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentServiceResponse;
import com.project.tour.service.tour.operation.OperationServiceTourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/service-tours")
public class OperationServiceTourController {

    private final OperationServiceTourService serviceTourService;

    public OperationServiceTourController(
            OperationServiceTourService serviceTourService) {

        this.serviceTourService = serviceTourService;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentServiceResponse>> getAll() {

        return ResponseEntity.ok(
                serviceTourService.getAll());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<AssignmentServiceResponse>> getByTourId(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                serviceTourService.getByTourId(tourId));
    }
}