package com.project.booking.controller.shore;

import com.project.booking.dto.onboard.ActivityVisitUsageRequest;
import com.project.booking.dto.shore.ActivityVisitUsageManagementResponse;
import com.project.booking.dto.shore.ActivityVisitUsageResponse;
import com.project.booking.service.shore.ActivityVisitUsageService;

import com.project.booking.service.command.IdempotentCommandService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shore/activity-visit-usages")
public class ActivityVisitUsageController {

    private final ActivityVisitUsageService activityVisitUsageService;
    private final IdempotentCommandService commands;

    public ActivityVisitUsageController(
            ActivityVisitUsageService activityVisitUsageService, IdempotentCommandService commands) {

        this.activityVisitUsageService = activityVisitUsageService;
        this.commands = commands;
    }

    @PostMapping
    @PreAuthorize("hasRole('SHORE')")
    public ResponseEntity<ActivityVisitUsageResponse> create(
            @Valid @RequestBody ActivityVisitUsageRequest request) {

        return commands.execute("SHORE_USAGE", request.clientRequestId(), request, ActivityVisitUsageResponse.class, () -> activityVisitUsageService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('SHORE')")
    public ResponseEntity<List<ActivityVisitUsageManagementResponse>> getManagementUsages() {

        return ResponseEntity.ok(
                activityVisitUsageService.getManagementUsages());
    }
}
