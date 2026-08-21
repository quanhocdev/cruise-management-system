package com.project.tour.controller.tour;

import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.service.tour.visit.ShoreTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/shore/tours")
public class ShoreTourConfigurationController {

    private final ShoreTourConfigurationService shoreTourConfigurationService;

    public ShoreTourConfigurationController(
            ShoreTourConfigurationService shoreTourConfigurationService) {

        this.shoreTourConfigurationService = shoreTourConfigurationService;
    }

    @GetMapping("/{tourId}/configuration")
    public ResponseEntity<ShoreTourConfigurationResponse> getConfiguration(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                shoreTourConfigurationService
                        .getConfiguration(tourId));
    }
}