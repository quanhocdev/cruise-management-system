package com.project.tour.controller;

import com.project.tour.dto.policy.*;
import com.project.tour.service.CancelPolicyService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/policies/{policyId}/cancel-rules")
public class CancelPolicyController {
    private final CancelPolicyService service;
    public CancelPolicyController(CancelPolicyService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<CancelPolicyResponse> create(@PathVariable UUID policyId, @Valid @RequestBody CreateCancelPolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(policyId, request));
    }
    @GetMapping
    public ResponseEntity<List<CancelPolicyResponse>> getAll(@PathVariable UUID policyId, @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(service.getAll(policyId, activeOnly));
    }
    @PutMapping("/{ruleId}")
    public ResponseEntity<CancelPolicyResponse> update(@PathVariable UUID policyId, @PathVariable UUID ruleId, @Valid @RequestBody UpdateCancelPolicyRequest request) {
        return ResponseEntity.ok(service.update(policyId, ruleId, request));
    }
    @PatchMapping("/{ruleId}/deactivate")
    public ResponseEntity<CancelPolicyResponse> deactivate(@PathVariable UUID policyId, @PathVariable UUID ruleId) {
        return ResponseEntity.ok(service.deactivate(policyId, ruleId));
    }
}
