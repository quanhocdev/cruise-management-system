package com.project.booking.controller.onboard;

import com.project.booking.dto.onboard.ActivityCruiseUsageRequest;
import com.project.booking.dto.onboard.ActivityCruiseUsageResponse;
import com.project.booking.service.onboard.ActivityCruiseUsageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboard/activity-cruise-usages")
public class ActivityCruiseUsageController {

    private final ActivityCruiseUsageService activityCruiseUsageService;

    public ActivityCruiseUsageController(
            ActivityCruiseUsageService activityCruiseUsageService) {

        this.activityCruiseUsageService = activityCruiseUsageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ONBOARD')")
    public ResponseEntity<ActivityCruiseUsageResponse> create(
            @Valid @RequestBody ActivityCruiseUsageRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activityCruiseUsageService.create(request));
    }

    @GetMapping("/booking-passenger/{bookingPassengerId}")
    @PreAuthorize("hasRole('ONBOARD')")
    public ResponseEntity<List<ActivityCruiseUsageResponse>> getByBookingPassenger(
            @PathVariable Long bookingPassengerId) {

        return ResponseEntity.ok(
                activityCruiseUsageService
                        .getByBookingPassengerId(bookingPassengerId));
    }
}