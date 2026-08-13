package com.project.tour.controller.service;

import com.project.tour.dto.service.area.CreateServiceAreaRequest;
import com.project.tour.dto.service.area.ServiceAreaResponse;
import com.project.tour.dto.service.area.UpdateServiceAreaRequest;
import com.project.tour.service.service.ServiceAreaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/service-areas")
@PreAuthorize("hasRole('CONVENIENCE')")
public class ServiceAreaController {

    private final ServiceAreaService serviceAreaService;

    public ServiceAreaController(
            ServiceAreaService serviceAreaService) {

        this.serviceAreaService = serviceAreaService;
    }

    /*
     * =====================================================
     * GÁN SERVICE VÀO AREA
     * =====================================================
     *
     * POST
     * /api/convenience/service-areas/areas/{areaId}
     */
    @PostMapping("/areas/{areaId}")
    public ResponseEntity<ServiceAreaResponse> createServiceArea(
            @PathVariable UUID areaId,
            @Valid @RequestBody CreateServiceAreaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        serviceAreaService.createServiceArea(
                                areaId,
                                request));
    }

    /*
     * =====================================================
     * LẤY SERVICE ĐÃ GÁN TRONG AREA
     * =====================================================
     *
     * GET
     * /api/convenience/service-areas/areas/{areaId}
     */
    @GetMapping("/areas/{areaId}")
    public ResponseEntity<List<ServiceAreaResponse>> getServicesByArea(
            @PathVariable UUID areaId) {

        return ResponseEntity.ok(
                serviceAreaService.getServicesByArea(
                        areaId));
    }

    /*
     * =====================================================
     * LẤY MỘT ASSIGNMENT
     * =====================================================
     */
    @GetMapping("/{serviceAreaId}")
    public ResponseEntity<ServiceAreaResponse> getServiceAreaById(
            @PathVariable UUID serviceAreaId,
            @RequestParam UUID areaId) {

        return ResponseEntity.ok(
                serviceAreaService.getServiceAreaById(
                        areaId,
                        serviceAreaId));
    }

    /*
     * =====================================================
     * LẤY CÁC AREA ĐANG DÙNG SERVICE
     * =====================================================
     *
     * GET
     * /api/convenience/service-areas/services/{serviceId}
     */
    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<ServiceAreaResponse>> getAreasByService(
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                serviceAreaService.getAreasByService(
                        serviceId));
    }

    /*
     * =====================================================
     * UPDATE
     * =====================================================
     */
    @PatchMapping("/{serviceAreaId}")
    public ResponseEntity<ServiceAreaResponse> updateServiceArea(
            @PathVariable UUID serviceAreaId,
            @RequestParam UUID areaId,
            @Valid @RequestBody UpdateServiceAreaRequest request) {

        return ResponseEntity.ok(
                serviceAreaService.updateServiceArea(
                        areaId,
                        serviceAreaId,
                        request));
    }

    /*
     * =====================================================
     * UNASSIGN SERVICE
     * =====================================================
     */
    @DeleteMapping("/{serviceAreaId}")
    public ResponseEntity<Void> deleteServiceArea(
            @PathVariable UUID serviceAreaId,
            @RequestParam UUID areaId) {

        serviceAreaService.deleteServiceArea(
                areaId,
                serviceAreaId);

        return ResponseEntity.noContent().build();
    }
}