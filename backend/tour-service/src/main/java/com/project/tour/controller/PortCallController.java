package com.project.tour.controller;

import com.project.tour.dto.portcall.*;
import com.project.tour.service.PortCallService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/schedules/{scheduleId}/days/{dayId}/port-calls")
public class PortCallController {
    private final PortCallService service;
    public PortCallController(PortCallService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<PortCallResponse> create(@PathVariable UUID scheduleId, @PathVariable UUID dayId,
                                                   @Valid @RequestBody CreatePortCallRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(scheduleId, dayId, request));
    }
    @GetMapping
    public ResponseEntity<List<PortCallResponse>> getAll(@PathVariable UUID scheduleId, @PathVariable UUID dayId) {
        return ResponseEntity.ok(service.getAll(scheduleId, dayId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PortCallResponse> get(@PathVariable UUID scheduleId, @PathVariable UUID dayId, @PathVariable UUID id) {
        return ResponseEntity.ok(service.get(scheduleId, dayId, id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PortCallResponse> update(@PathVariable UUID scheduleId, @PathVariable UUID dayId,
                                                   @PathVariable UUID id, @Valid @RequestBody UpdatePortCallRequest request) {
        return ResponseEntity.ok(service.update(scheduleId, dayId, id, request));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PortCallResponse> cancel(@PathVariable UUID scheduleId, @PathVariable UUID dayId, @PathVariable UUID id) {
        return ResponseEntity.ok(service.cancel(scheduleId, dayId, id));
    }
}
