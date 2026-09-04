package com.project.convenience.controller.product.convenience;

import com.project.convenience.dto.product.convenience.ProductTourConfigRequest;
import com.project.convenience.dto.product.convenience.ProductTourResponse;
import com.project.convenience.dto.product.convenience.HistoryProductTourResponse;
import com.project.convenience.service.product.ProductTourConfigService;
import com.project.convenience.service.product.ProductTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/product-tours")
public class ProductTourController {

        private final ProductTourService productTourService;
        private final ProductTourConfigService configService;

        public ProductTourController(
                        ProductTourService productTourService,
                        ProductTourConfigService configService) {

                this.productTourService = productTourService;
                this.configService = configService;
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
        // GET CONFIGURATION HISTORY
        // =====================================================

        @GetMapping("/configuration-history")
        public ResponseEntity<List<HistoryProductTourResponse>> getConfigurationHistory() {

                return ResponseEntity.ok(
                                productTourService.getConfigurationHistory());
        }

        // =====================================================
        // GET CONFIGURATION DETAIL BY TOUR
        // =====================================================

        @GetMapping("/tour/{tourId}")
        public ResponseEntity<List<ProductTourResponse>> getConfigurationDetail(
                        @PathVariable UUID tourId) {

                return ResponseEntity.ok(
                                productTourService.getConfigurationHistoryDetail(tourId));
        }
        // =====================================================
        // POST CONFIG
        // =====================================================

        @PostMapping("/{assignmentId}/config")
        public ResponseEntity<ProductTourResponse> configure(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ProductTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.configure(
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
                                configService.updateConfig(
                                                assignmentId,
                                                request));
        }
}