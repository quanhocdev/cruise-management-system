package com.project.tour.controller.onboard;

import com.project.tour.dto.onboard.ActivityCruiseResponse;
import com.project.tour.dto.onboard.CreateActivityCruiseRequest;
import com.project.tour.dto.onboard.UpdateActivityCruiseRequest;
import com.project.tour.service.onboard.ActivityCruiseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboard/activities-cruise")
public class ActivityCruiseController {

    private final ActivityCruiseService activityCruiseService;

    public ActivityCruiseController(ActivityCruiseService activityCruiseService) {
        this.activityCruiseService = activityCruiseService;
    }

    @GetMapping
    public ResponseEntity<Page<ActivityCruiseResponse>> getAllActivities(
            @RequestParam(required = false) Long cruiseAreaId,
            Pageable pageable) {
        if (cruiseAreaId != null) {
            return ResponseEntity.ok(activityCruiseService.getActivitiesByArea(cruiseAreaId, pageable));
        }
        return ResponseEntity.ok(activityCruiseService.getAllActivities(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityCruiseResponse> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityCruiseService.getActivityById(id));
    }

    @PostMapping
    public ResponseEntity<ActivityCruiseResponse> createActivity(
            @Valid @RequestBody CreateActivityCruiseRequest request) {
        ActivityCruiseResponse created = activityCruiseService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityCruiseResponse> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateActivityCruiseRequest request) {
        return ResponseEntity.ok(activityCruiseService.updateActivity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityCruiseService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}