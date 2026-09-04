package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse; // Fix import DTO
import com.project.tour.service.tour.operation.assignment.ProductTourAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation/product-tour-assignment")
public class ProductTourAssignmentController {

    private final ProductTourAssignmentService assignmentService;

    public ProductTourAssignmentController(ProductTourAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Operation phân công khu vực tiện ích cho Tour.
     */
    @PostMapping
    public ResponseEntity<ProductTourAssignmentResponse> assign(
            @Valid @RequestBody ProductTourAssignmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assignmentService.assign(request));
    }

    /**
     * Lấy danh sách phân công tiện ích của một Tour.
     */
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ProductTourAssignmentResponse>> getByTour(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(assignmentService.getByTour(tourId));
    }

    /**
     * Xóa phân công tiện ích theo tourId và cruiseAreaId
     * URL: DELETE
     * /api/operation/product-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
     */
    @DeleteMapping("/tour/{tourId}/area/{cruiseAreaId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tourId,
            @PathVariable UUID cruiseAreaId) {

        assignmentService.deleteAssignment(tourId, cruiseAreaId);

        return ResponseEntity.noContent().build();
    }
}