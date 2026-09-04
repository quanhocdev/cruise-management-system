package com.project.convenience.controller.service.convenience;

import com.project.convenience.dto.service.convenience.ServiceTourConfigRequest;
import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.dto.service.convenience.HistoryServiceTourResponse;
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

        @GetMapping
        public ResponseEntity<List<ServiceTourResponse>> getAllAssignments() {

                return ResponseEntity.ok(
                                serviceTourService.getAllAssignments());
        }

        @GetMapping("/pending-config")
        public ResponseEntity<List<ServiceTourResponse>> getPendingConfig() {

                return ResponseEntity.ok(
                                serviceTourService.getPendingConfig());
        }

        @GetMapping("/configuration-history")
        public ResponseEntity<List<HistoryServiceTourResponse>> getConfigurationHistory() {

                return ResponseEntity.ok(
                                serviceTourService.getConfigurationHistory());
        }

        @GetMapping("/tour/{tourId}")
        public ResponseEntity<List<ServiceTourResponse>> getConfigurationHistoryDetail(
                        @PathVariable UUID tourId) {

                return ResponseEntity.ok(
                                serviceTourService.getConfigurationHistoryDetail(tourId));
        }

        @PostMapping("/{assignmentId}/config")
        public ResponseEntity<ServiceTourResponse> configure(
                        @PathVariable UUID assignmentId,
                        @Valid @RequestBody ServiceTourConfigRequest request) {

                return ResponseEntity.ok(
                                configService.configure(
                                                assignmentId,
                                                request));
        }

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