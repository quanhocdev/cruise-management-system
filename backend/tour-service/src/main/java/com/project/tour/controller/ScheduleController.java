package com.project.tour.controller;

import com.project.tour.dto.schedule.*;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService service;
    public ScheduleController(ScheduleService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<ScheduleResponse> create(@Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAll(
        @RequestParam(required = false) ScheduleStatus status,
        @RequestParam(defaultValue = "false") boolean upcomingOnly
    ) { return ResponseEntity.ok(service.getAll(status, upcomingOnly)); }
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<ScheduleResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateScheduleRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScheduleResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancel(id));
    }
}
