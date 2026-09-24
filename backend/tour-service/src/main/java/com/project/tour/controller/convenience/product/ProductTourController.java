package com.project.tour.controller.convenience.product;

import com.project.tour.dto.convenience.product.convenience.ProductTourConfigRequest;
import com.project.tour.dto.convenience.product.convenience.ProductTourResponse;
import com.project.tour.service.convenience.product.ProductTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/product-tours")
public class ProductTourController {

    private final ProductTourService productTourService;

    public ProductTourController(
            ProductTourService productTourService) {

        this.productTourService = productTourService;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ProductTourResponse>> getAllAssignments() {

        return ResponseEntity.ok(
                productTourService.getAllAssignments());
    }

    // =====================================================
    // GET PENDING CONFIG
    // =====================================================

    @GetMapping("/pending-config")
    public ResponseEntity<List<ProductTourResponse>> getPendingConfig() {

        return ResponseEntity.ok(
                productTourService.getPendingConfig());
    }

    // =====================================================
    // GET CONFIGURATION DETAIL BY TOUR
    // =====================================================

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ProductTourResponse>> getConfigurationDetail(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                productTourService.getByTour(tourId));
    }

    // =====================================================
    // POST CONFIG
    // =====================================================

    @PostMapping("/{assignmentId}/config")
    public ResponseEntity<ProductTourResponse> configure(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ProductTourConfigRequest request) {

        return ResponseEntity.ok(
                productTourService.configure(
                        assignmentId,
                        request));
    }

    // =====================================================
    // PATCH CONFIG
    // =====================================================

    @PatchMapping("/{assignmentId}/config")
    public ResponseEntity<ProductTourResponse> updateConfig(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ProductTourConfigRequest request) {

        return ResponseEntity.ok(
                productTourService.updateConfig(
                        assignmentId,
                        request));
    }

    // =====================================================
    // COMPLETE CONFIGURATION
    // =====================================================

    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> completeConfiguration(
            @PathVariable UUID tourId) {

        productTourService.completeConfiguration(tourId);

        return ResponseEntity.ok().build();
    }
}