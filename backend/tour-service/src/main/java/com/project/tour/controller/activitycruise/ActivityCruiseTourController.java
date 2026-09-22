package com.project.tour.controller.activitycruise;

import com.project.tour.dto.activitycruise.

import com.project.tour.dto.activitycruise.HistoryActivityCruiseTourResponse;
import com.project.tour.service.activitycruise.ActivityCruiseTourConfigService;
import com.project.tour.service.activitycruise.ActivityCruiseTourService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/onboard/activity-cruise-tours")
public class ActivityCruiseTourController {

    private final ActivityCruiseTourService activityCruiseTourService;
    private final ActivityCruiseTourConfigService configService;

    public ActivityCruiseTourController(
            ActivityCruiseTourService activityCruiseTourService,
            ActivityCruiseTourConfigService configService) {

        this.activityCruiseTourService = activityCruiseTourService;
        this.configService = configService;
    }

    // =====================================================
    // GET ALL ASSIGNMENTS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ActivityCruiseTourResponse>> getAllAssignments() {

        return ResponseEntity.ok(
                activityCruiseTourService.getAllAssignments());
    }

    // =====================================================
    // GET ASSIGNMENTS ĐANG CHỜ CẤU HÌNH
    // =====================================================

    @GetMapping("/pending-config")
    public ResponseEntity<List<ActivityCruiseTourResponse>> getPendingConfig() {

        return ResponseEntity.ok(
                activityCruiseTourService.getPendingConfig());
    }

    // =====================================================
    // GET CONFIGURATION HISTORY
    // =====================================================

    @GetMapping("/configuration-history")
    public ResponseEntity<List<HistoryActivityCruiseTourResponse>> getConfigurationHistory() {

        return ResponseEntity.ok(
                activityCruiseTourService.getConfigurationHistory());
    }

    // =====================================================
    // GET CONFIGURATION DETAIL BY TOUR
    // =====================================================

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ActivityCruiseTourResponse>> getConfigurationDetail(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                activityCruiseTourService.getConfigurationDetail(tourId));
    }

    // =====================================================
    // CREATE CONFIG
    // =====================================================

    @PostMapping("/{assignmentId}/config")
    public ResponseEntity<ActivityCruiseTourResponse> configure(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ActivityCruiseTourConfigRequest request) {

        return ResponseEntity.ok(
                configService.configure(
                        assignmentId,
                        request));
    }

    // =====================================================
    // UPDATE CONFIG
    // =====================================================

    @PatchMapping("/{assignmentId}/config")
    public ResponseEntity<ActivityCruiseTourResponse> updateConfig(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ActivityCruiseTourConfigRequest request) {

        return ResponseEntity.ok(
                configService.updateConfig(
                        assignmentId,
                        request));
    }
}
