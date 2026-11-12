
package com.project.tour.controller.admin;

import com.project.tour.dto.request.admin.PolicyRefundRuleRequest;
import com.project.tour.dto.response.admin.PolicyRefundRuleResponse;
import com.project.tour.service.policy.PolicyRefundRuleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/policies/{policyId}/refund-rules")
public class PolicyRefundRuleController {

    private final PolicyRefundRuleService policyRefundRuleService;

    public PolicyRefundRuleController(
            PolicyRefundRuleService policyRefundRuleService) {
        this.policyRefundRuleService = policyRefundRuleService;
    }

    @PostMapping
    public ResponseEntity<PolicyRefundRuleResponse> createRule(
            @PathVariable UUID policyId,
            @Valid @RequestBody PolicyRefundRuleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(policyRefundRuleService.create(policyId, request));
    }

    @GetMapping
    public ResponseEntity<List<PolicyRefundRuleResponse>> getRulesByPolicyId(
            @PathVariable UUID policyId) {
        return ResponseEntity.ok(
                policyRefundRuleService.getByPolicyId(policyId));
    }

    @PatchMapping("/{ruleId}")
    public ResponseEntity<PolicyRefundRuleResponse> updateRule(
            @PathVariable UUID policyId,
            @PathVariable UUID ruleId,
            @Valid @RequestBody PolicyRefundRuleRequest request) {
        return ResponseEntity.ok(
                policyRefundRuleService.update(
                        policyId,
                        ruleId,
                        request));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable UUID policyId,
            @PathVariable UUID ruleId) {
        policyRefundRuleService.delete(policyId, ruleId);
        return ResponseEntity.noContent().build();
    }
}
