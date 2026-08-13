// package com.project.tour.controller.schedule;

// import com.project.tour.dto.itinerary.*;
// import com.project.tour.service.schedule.ItineraryDayService;

// import jakarta.validation.Valid;
// import org.springframework.http.*;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/v1/schedules/{scheduleId}/days")
// public class ItineraryDayController {
// private final ItineraryDayService service;

// public ItineraryDayController(ItineraryDayService service) {
// this.service = service;
// }

// @PostMapping
// public ResponseEntity<ItineraryDayResponse> create(@PathVariable UUID
// scheduleId,
// @Valid @RequestBody CreateItineraryDayRequest request) {
// return
// ResponseEntity.status(HttpStatus.CREATED).body(service.create(scheduleId,
// request));
// }

// @GetMapping
// public ResponseEntity<List<ItineraryDayResponse>> getAll(@PathVariable UUID
// scheduleId) {
// return ResponseEntity.ok(service.getAll(scheduleId));
// }

// @GetMapping("/{id}")
// public ResponseEntity<ItineraryDayResponse> get(@PathVariable UUID
// scheduleId, @PathVariable UUID id) {
// return ResponseEntity.ok(service.get(scheduleId, id));
// }

// @PutMapping("/{id}")
// public ResponseEntity<ItineraryDayResponse> update(@PathVariable UUID
// scheduleId, @PathVariable UUID id,
// @Valid @RequestBody UpdateItineraryDayRequest request) {
// return ResponseEntity.ok(service.update(scheduleId, id, request));
// }

// @DeleteMapping("/{id}")
// public ResponseEntity<Void> delete(@PathVariable UUID scheduleId,
// @PathVariable UUID id) {
// service.delete(scheduleId, id);
// return ResponseEntity.noContent().build();
// }
// }
