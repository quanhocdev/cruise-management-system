// package com.project.tour.controller.tour;

// import com.project.tour.dto.tour.schedule.CreateScheduleRequest;
// import com.project.tour.dto.tour.schedule.ScheduleResponse;
// import com.project.tour.dto.tour.schedule.UpdateScheduleRequest;
// import com.project.tour.service.service.schedule.ScheduleService;

// import jakarta.validation.Valid;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/scheduler/tours/{tourId}/schedules")
// public class ScheduleController {

// private final ScheduleService scheduleService;

// public ScheduleController(
// ScheduleService scheduleService) {

// this.scheduleService = scheduleService;
// }

// @PostMapping
// public ResponseEntity<ScheduleResponse> create(
// @PathVariable UUID tourId,
// @Valid @RequestBody CreateScheduleRequest request) {

// return ResponseEntity
// .status(HttpStatus.CREATED)
// .body(scheduleService.create(
// tourId,
// request));
// }

// @GetMapping
// public ResponseEntity<List<ScheduleResponse>> getAll(
// @PathVariable UUID tourId,
// @RequestParam(defaultValue = "false") boolean activeOnly) {

// List<ScheduleResponse> response = activeOnly
// ? scheduleService.getActive(tourId)
// : scheduleService.getAll(tourId);

// return ResponseEntity.ok(response);
// }

// @GetMapping("/{scheduleId}")
// public ResponseEntity<ScheduleResponse> getById(
// @PathVariable UUID tourId,
// @PathVariable UUID scheduleId) {

// return ResponseEntity.ok(
// scheduleService.getById(
// tourId,
// scheduleId));
// }

// @PatchMapping("/{scheduleId}")
// public ResponseEntity<ScheduleResponse> update(
// @PathVariable UUID tourId,
// @PathVariable UUID scheduleId,
// @Valid @RequestBody UpdateScheduleRequest request) {

// return ResponseEntity.ok(
// scheduleService.update(
// tourId,
// scheduleId,
// request));
// }

// @DeleteMapping("/{scheduleId}")
// public ResponseEntity<Void> delete(
// @PathVariable UUID tourId,
// @PathVariable UUID scheduleId) {

// scheduleService.delete(
// tourId,
// scheduleId);

// return ResponseEntity.noContent().build();
// }
// }