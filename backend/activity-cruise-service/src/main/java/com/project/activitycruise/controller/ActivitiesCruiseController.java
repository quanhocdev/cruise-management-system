package com.project.activitycruise.controller;

import com.project.activitycruise.dto.ActivityCruiseResponse;
import com.project.activitycruise.dto.CreateActivityCruiseRequest;
import com.project.activitycruise.dto.UpdateActivityCruiseRequest;
import com.project.activitycruise.service.ActivityCruiseService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/onboard/activities-cruise")
public class ActivitiesCruiseController {

    private final ActivityCruiseService activityCruiseService;

    public ActivitiesCruiseController(ActivityCruiseService activityCruiseService) {
        this.activityCruiseService = activityCruiseService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityCruiseResponse> createActivity(
            @Valid @ModelAttribute CreateActivityCruiseRequest request) {

        ActivityCruiseResponse response = activityCruiseService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityCruiseResponse> getActivityById(@PathVariable UUID id) {

        return ResponseEntity.ok(activityCruiseService.getActivityById(id));
    }

    @GetMapping
    public ResponseEntity<List<ActivityCruiseResponse>> getActivities() {

        return ResponseEntity.ok(activityCruiseService.getActivities());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActivityCruiseResponse>> getActiveActivities() {

        return ResponseEntity.ok(activityCruiseService.getActiveActivities());
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityCruiseResponse> updateActivity(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateActivityCruiseRequest request) {

        return ResponseEntity.ok(activityCruiseService.updateActivity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {

        activityCruiseService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}