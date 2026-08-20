package com.project.tour.controller.tour.convenience;

import com.project.tour.dto.tour.convenience.product.ProductTourConfigRequest;
import com.project.tour.dto.tour.convenience.product.ProductTourResponse;
import com.project.tour.service.tour.convenience.ProductTourConfigService;
import com.project.tour.service.tour.convenience.ProductTourService;

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
        // GET PENDING CONFIG
        // =====================================================

        /**
         * Lấy các ProductTour đang chờ cấu hình.
         *
         * Điều kiện:
         * - Tour = APPROVED
         * - ProductTour = WAITING_CONFIG
         */
        @GetMapping("/pending-config")
        public ResponseEntity<List<ProductTourResponse>> getPendingConfig() {

                return ResponseEntity.ok(
                                productTourService.getPendingConfig());
        }

        // =====================================================
        // POST CONFIG
        // =====================================================

        /**
         * Cấu hình ProductTour lần đầu.
         *
         * WAITING_CONFIG -> NOT_STARTED
         */
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

        /**
         * Cập nhật cấu hình ProductTour.
         *
         * Chỉ cho phép khi:
         * NOT_STARTED
         */
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