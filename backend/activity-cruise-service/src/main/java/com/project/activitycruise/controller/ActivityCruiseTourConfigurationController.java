package com.project.activitycruise.controller;

import com.project.activitycruise.service.ActivityCruiseTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/activity-cruise/tour-configurations")
public class ActivityCruiseTourConfigurationController {

    private final ActivityCruiseTourConfigurationService configurationService;

    public ActivityCruiseTourConfigurationController(
            ActivityCruiseTourConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    @PostMapping("/{assignmentId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID assignmentId) {

        configurationService.complete(assignmentId);

        return ResponseEntity.ok().build();
    }
}