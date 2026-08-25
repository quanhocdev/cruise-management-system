package com.project.convenience.controller.service.convenience;

import com.project.convenience.service.service.ServiceTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/service-tours")
public class ServiceTourConfigurationController {

    private final ServiceTourConfigurationService configurationService;

    public ServiceTourConfigurationController(
            ServiceTourConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> completeConfiguration(
            @PathVariable UUID tourId) {

        configurationService.completeConfiguration(tourId);

        return ResponseEntity.ok().build();
    }
}