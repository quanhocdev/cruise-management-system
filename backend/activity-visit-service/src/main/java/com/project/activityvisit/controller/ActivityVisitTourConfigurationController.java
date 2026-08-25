package com.project.activityvisit.controller;

import com.project.activityvisit.service.ActivityVisitTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/activity-visit/tour-configurations")
public class ActivityVisitTourConfigurationController {

    private final ActivityVisitTourConfigurationService configurationService;

    public ActivityVisitTourConfigurationController(
            ActivityVisitTourConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    /**
     * Hoàn thành cấu hình tất cả VisitTour của một Tour.
     */
    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID tourId) {

        configurationService.complete(tourId);

        return ResponseEntity.ok().build();
    }
}