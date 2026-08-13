package com.project.tour.controller.service;

import com.project.tour.dto.service.CreateServiceRequest;
import com.project.tour.dto.service.ServiceResponse;
import com.project.tour.dto.service.UpdateServiceRequest;
import com.project.tour.service.service.ServiceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/services")
public class ServiceController {

        private final ServiceService serviceService;

        public ServiceController(
                        ServiceService serviceService) {

                this.serviceService = serviceService;
        }

        /*
         * =====================================================
         * CREATE
         * =====================================================
         */
        @PostMapping
        public ResponseEntity<ServiceResponse> createService(
                        @Valid @ModelAttribute CreateServiceRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                serviceService.createService(
                                                                request));
        }

        /*
         * =====================================================
         * GET ALL
         * =====================================================
         */
        @GetMapping
        public ResponseEntity<List<ServiceResponse>> getServices(
                        @RequestParam(defaultValue = "false") boolean activeOnly) {

                return ResponseEntity.ok(
                                serviceService.getServices(
                                                activeOnly));
        }

        /*
         * =====================================================
         * GET BY ID
         * =====================================================
         */
        @GetMapping("/{serviceId}")
        public ResponseEntity<ServiceResponse> getServiceById(
                        @PathVariable UUID serviceId) {

                return ResponseEntity.ok(
                                serviceService.getServiceById(
                                                serviceId));
        }

        /*
         * =====================================================
         * UPDATE
         * =====================================================
         */
        @PatchMapping("/{serviceId}")
        public ResponseEntity<ServiceResponse> updateService(
                        @PathVariable UUID serviceId,
                        @Valid @ModelAttribute UpdateServiceRequest request) {

                return ResponseEntity.ok(
                                serviceService.updateService(
                                                serviceId,
                                                request));
        }

        /*
         * =====================================================
         * DELETE
         * =====================================================
         */
        @DeleteMapping("/{serviceId}")
        public ResponseEntity<Void> deleteService(
                        @PathVariable UUID serviceId) {

                serviceService.deleteService(
                                serviceId);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}