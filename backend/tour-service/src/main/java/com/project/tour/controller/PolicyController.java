package com.project.tour.controller;

import com.project.tour.dto.policy.*;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/policies")
public class PolicyController {
    private final PolicyService service;
    public PolicyController(PolicyService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody CreatePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAll(
        @RequestParam(required = false) PolicyType type,
        @RequestParam(defaultValue = "false") boolean activeOnly
    ) { return ResponseEntity.ok(service.getAll(type, activeOnly)); }
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdatePolicyRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PolicyResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(service.deactivate(id));
    }
}
