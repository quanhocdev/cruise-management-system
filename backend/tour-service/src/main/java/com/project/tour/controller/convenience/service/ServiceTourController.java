package com.project.tour.controller.convenience.service;

import com.project.tour.dto.convenience.service.convenience.ServiceTourConfigRequest;
import com.project.tour.dto.convenience.service.convenience.ServiceTourResponse;
import com.project.tour.service.convenience.service.ServiceTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/service-tours")
public class ServiceTourController {

    private final ServiceTourService serviceTourService;

    public ServiceTourController(
            ServiceTourService serviceTourService) {

        this.serviceTourService = serviceTourService;
    }

    /*
     * =====================================================
     * GET ALL
     * =====================================================
     */
    @GetMapping
    public ResponseEntity<List<ServiceTourResponse>> getAllAssignments() {

        return ResponseEntity.ok(
                serviceTourService.getAllAssignments());
    }

    /*
     * =====================================================
     * GET PENDING CONFIGURATION
     * =====================================================
     */
    @GetMapping("/pending-config")
    public ResponseEntity<List<ServiceTourResponse>> getPendingConfig() {

        return ResponseEntity.ok(
                serviceTourService.getPendingConfig());
    }

    /*
     * =====================================================
     * GET CONFIGURATION DETAIL BY TOUR
     * =====================================================
     */
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ServiceTourResponse>> getConfigurationDetail(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                serviceTourService.getByTour(tourId));
    }

    /*
     * =====================================================
     * CONFIGURE
     * =====================================================
     */
    @PostMapping("/{assignmentId}/config")
    public ResponseEntity<ServiceTourResponse> configure(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ServiceTourConfigRequest request) {

        return ResponseEntity.ok(
                serviceTourService.configure(
                        assignmentId,
                        request));
    }

    /*
     * =====================================================
     * UPDATE CONFIGURATION
     * =====================================================
     */
    @PatchMapping("/{assignmentId}/config")
    public ResponseEntity<ServiceTourResponse> updateConfig(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ServiceTourConfigRequest request) {

        return ResponseEntity.ok(
                serviceTourService.updateConfig(
                        assignmentId,
                        request));
    }

    /*
     * =====================================================
     * COMPLETE CONFIGURATION
     * =====================================================
     */
    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> completeConfiguration(
            @PathVariable UUID tourId) {

        serviceTourService.completeConfiguration(
                tourId);

        return ResponseEntity.ok().build();
    }
}