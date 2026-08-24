package com.project.convenience.controller.service.convenience;

import com.project.convenience.dto.service.convenience.ServiceTourConfigRequest;
import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.service.service.ServiceTourConfigService;
import com.project.convenience.service.service.ServiceTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/service-tours")
public class ServiceTourController {

        private final ServiceTourService serviceTourService;
        private final ServiceTourConfigService configService;

        public ServiceTourController(
                        ServiceTourService serviceTourService,
                        ServiceTourConfigService configService) {

                this.serviceTourService = serviceTourService;
                this.configService = configService;
        }

        // =====================================================
        // GET PENDING CONFIG
        // =====================================================

        /**
         * Lấy các ServiceTour đang chờ Convenience cấu hình.
         *
         * Điều kiện:
         * - Tour = APPROVED
         * - ServiceTour = WAITING_CONFIG
         */
        @GetMapping("/pending-config")
        public ResponseEntity<List<ServiceTourResponse>> getPendingConfig() {

                return ResponseEntity.ok(
                                serviceTourService.getPendingConfig());
        }

        // =====================================================
        // POST CONFIG
        // =====================================================

        /**
         * Cấu hình ServiceTour lần đầu.
         *
         * WAITING_CONFIG -> NOT_STARTED
         */
        @PostMapping("/{assignmentId}/config")
        public ResponseEntity<ServiceTourResponse> configure(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ServiceTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.configure(
                                                assignmentId,
                                                request));
        }

        // =====================================================
        // PATCH CONFIG
        // =====================================================

        /**
         * Cập nhật cấu hình ServiceTour.
         *
         * Chỉ cho phép khi:
         * NOT_STARTED
         */
        @PatchMapping("/{assignmentId}/config")
        public ResponseEntity<ServiceTourResponse> updateConfig(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ServiceTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.updateConfig(
                                                assignmentId,
                                                request));
        }
}