package com.project.convenience.controller.service.convenience;

import com.project.convenience.dto.service.convenience.ServiceConvenienceResponse;
import com.project.convenience.service.service.ServiceConvenienceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/services")
public class ServiceConvenienceController {

    private final ServiceConvenienceService serviceConvenienceService;

    public ServiceConvenienceController(
            ServiceConvenienceService serviceConvenienceService) {
        this.serviceConvenienceService = serviceConvenienceService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceConvenienceResponse>> getServices() {
        return ResponseEntity.ok(
                serviceConvenienceService.getActiveServices());
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceConvenienceResponse> getServiceById(
            @PathVariable UUID serviceId) {
        return ResponseEntity.ok(
                serviceConvenienceService.getServiceById(serviceId));
    }
}