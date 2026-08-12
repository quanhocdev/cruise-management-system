package com.project.tour.controller.policy;

import com.project.tour.dto.policy.CancelPolicyResponse;
import com.project.tour.dto.policy.CreateCancelPolicyRequest;
import com.project.tour.dto.policy.UpdateCancelPolicyRequest;
import com.project.tour.service.policy.CancelPolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/policies/{policyId}/cancel-rules")
public class CancelPolicyController {

    private final CancelPolicyService service;

    public CancelPolicyController(
            CancelPolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CancelPolicyResponse> create(
            @PathVariable UUID policyId,
            @Valid @RequestBody CreateCancelPolicyRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(policyId, request));
    }

    @GetMapping
    public ResponseEntity<List<CancelPolicyResponse>> getAll(
            @PathVariable UUID policyId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        return ResponseEntity.ok(
                service.getAll(policyId, activeOnly));
    }

    @PatchMapping("/{ruleId}")
    public ResponseEntity<CancelPolicyResponse> update(
            @PathVariable UUID policyId,
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdateCancelPolicyRequest request) {

        return ResponseEntity.ok(
                service.update(policyId, ruleId, request));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID policyId,
            @PathVariable UUID ruleId) {

        service.deactivate(policyId, ruleId);

        return ResponseEntity.noContent().build();
    }
}