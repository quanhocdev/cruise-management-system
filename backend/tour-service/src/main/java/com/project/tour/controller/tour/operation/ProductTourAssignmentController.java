package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.ProductTourAssignmentRequest;
import com.project.tour.dto.tour.operation.ProductTourAssignmentResponse;
import com.project.tour.service.tour.operation.ProductTourAssignmentService;

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

    public ProductTourAssignmentController(
            ProductTourAssignmentService assignmentService) {

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

        return ResponseEntity.ok(
                assignmentService.getByTour(tourId));
    }

    /**
     * Xóa phân công tiện ích khi vẫn đang WAITING_CONFIG.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        assignmentService.deleteAssignment(id);

        return ResponseEntity.noContent().build();
    }
}