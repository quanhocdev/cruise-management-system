package com.project.tour.controller.policy;

import com.project.tour.dto.policy.CreatePolicyRequest;
import com.project.tour.dto.policy.PolicyResponse;
import com.project.tour.dto.policy.UpdatePolicyRequest;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.service.policy.PolicyService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PolicyResponse> create(
            @Valid @RequestBody CreatePolicyRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(policyService.create(request));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAll(
            @RequestParam(required = false) PolicyType type,
            @RequestParam(required = false) PolicyStatus status) {

        return ResponseEntity.ok(
                policyService.getAll(type, status));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                policyService.getById(id));
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<PolicyResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePolicyRequest request) {

        return ResponseEntity.ok(
                policyService.update(id, request));
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id) {

        policyService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}