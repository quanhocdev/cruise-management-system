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
@RequestMapping("/api/admin/areas/{areaId}/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(
            ServiceService serviceService) {

        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @PathVariable UUID areaId,
            @Valid @RequestBody CreateServiceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceService.createService(
                        areaId,
                        request));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getServices(
            @PathVariable UUID areaId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        return ResponseEntity.ok(
                serviceService.getServices(
                        areaId,
                        activeOnly));
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceResponse> getServiceById(
            @PathVariable UUID areaId,
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                serviceService.getServiceById(
                        areaId,
                        serviceId));
    }

    @PatchMapping("/{serviceId}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable UUID areaId,
            @PathVariable UUID serviceId,
            @Valid @RequestBody UpdateServiceRequest request) {

        return ResponseEntity.ok(
                serviceService.updateService(
                        areaId,
                        serviceId,
                        request));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable UUID areaId,
            @PathVariable UUID serviceId) {

        serviceService.deleteService(
                areaId,
                serviceId);

        return ResponseEntity.noContent().build();
    }
}