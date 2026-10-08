package com.project.tour.controller.internal;

import com.project.tour.dto.internal.ActivityCruiseUsageBatchRequest;
import com.project.tour.dto.internal.ActivityCruiseUsageInfo;
import com.project.tour.dto.internal.ActivityCruiseUsageManagementInfo;
import com.project.tour.service.internal.ActivityCruiseTourInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/activity-cruise-tours")
public class ActivityCruiseTourInternalController {

    private final ActivityCruiseTourInternalService activityCruiseTourInternalService;

    public ActivityCruiseTourInternalController(
            ActivityCruiseTourInternalService activityCruiseTourInternalService) {

        this.activityCruiseTourInternalService = activityCruiseTourInternalService;
    }

    @GetMapping("/{activityCruiseTourId}/usage-info")
    public ResponseEntity<ActivityCruiseUsageInfo> getActivityCruiseUsageInfo(
            @PathVariable UUID activityCruiseTourId,
            @RequestParam UUID tourPackageId) {

        return ResponseEntity.ok(
                activityCruiseTourInternalService.getActivityCruiseUsageInfo(
                        activityCruiseTourId,
                        tourPackageId));
    }

    @PostMapping("/usage-info/batch")
    public ResponseEntity<List<ActivityCruiseUsageManagementInfo>> getActivityCruiseUsageManagementInfo(
            @RequestBody ActivityCruiseUsageBatchRequest request) {

        return ResponseEntity.ok(
                activityCruiseTourInternalService
                        .getActivityCruiseUsageManagementInfo(
                                request.activityCruiseTourIds()));
    }
}