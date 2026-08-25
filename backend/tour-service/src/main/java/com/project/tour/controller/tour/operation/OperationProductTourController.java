package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.AssignmentProductResponse;
import com.project.tour.service.tour.operation.OperationProductTourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/product-tours")
public class OperationProductTourController {

    private final OperationProductTourService operationProductTourService;

    public OperationProductTourController(
            OperationProductTourService operationProductTourService) {

        this.operationProductTourService = operationProductTourService;
    }

    @GetMapping
    public ResponseEntity<List<AssignmentProductResponse>> getAll() {

        return ResponseEntity.ok(
                operationProductTourService.getAll());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<AssignmentProductResponse>> getByTourId(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                operationProductTourService
                        .getProductToursByTourId(tourId));
    }
}