package com.project.booking.controller;

import com.project.booking.dto.ActivityVisitUsageRequest;
import com.project.booking.dto.ActivityVisitUsageResponse;
import com.project.booking.service.ActivityVisitUsageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shore/activity-visit-usages")
public class ActivityVisitUsageController {

    private final ActivityVisitUsageService activityVisitUsageService;

    public ActivityVisitUsageController(
            ActivityVisitUsageService activityVisitUsageService) {

        this.activityVisitUsageService = activityVisitUsageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SHORE')")
    public ResponseEntity<ActivityVisitUsageResponse> create(
            @Valid @RequestBody ActivityVisitUsageRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activityVisitUsageService.create(request));
    }
}