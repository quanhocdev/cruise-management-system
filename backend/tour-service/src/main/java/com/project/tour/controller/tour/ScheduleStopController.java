package com.project.tour.controller.tour;

import com.project.tour.dto.tour.schedule.stop.CreateScheduleStopRequest;
import com.project.tour.dto.tour.schedule.stop.ScheduleStopResponse;
import com.project.tour.dto.tour.schedule.stop.UpdateScheduleStopRequest;
import com.project.tour.service.tour.schedule.stop.ScheduleStopService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scheduler/schedules/{scheduleId}/stops")
public class ScheduleStopController {

    private final ScheduleStopService scheduleStopService;

    public ScheduleStopController(
            ScheduleStopService scheduleStopService) {

        this.scheduleStopService = scheduleStopService;
    }

    @PostMapping
    public ResponseEntity<ScheduleStopResponse> create(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody CreateScheduleStopRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(scheduleStopService.create(
                        scheduleId,
                        request));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleStopResponse>> getAll(
            @PathVariable UUID scheduleId) {

        return ResponseEntity.ok(
                scheduleStopService.getAll(scheduleId));
    }

    @GetMapping("/{stopId}")
    public ResponseEntity<ScheduleStopResponse> getById(
            @PathVariable UUID scheduleId,
            @PathVariable UUID stopId) {

        return ResponseEntity.ok(
                scheduleStopService.getById(
                        scheduleId,
                        stopId));
    }

    @PatchMapping("/{stopId}")
    public ResponseEntity<ScheduleStopResponse> update(
            @PathVariable UUID scheduleId,
            @PathVariable UUID stopId,
            @Valid @RequestBody UpdateScheduleStopRequest request) {

        return ResponseEntity.ok(
                scheduleStopService.update(
                        scheduleId,
                        stopId,
                        request));
    }

    @DeleteMapping("/{stopId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID scheduleId,
            @PathVariable UUID stopId) {

        scheduleStopService.delete(
                scheduleId,
                stopId);

        return ResponseEntity.noContent().build();
    }
}