package com.project.tour.service;

import com.project.tour.dto.policy.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.CancelPolicy;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.CancelPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @Transactional
public class CancelPolicyService {
    private final CancelPolicyRepository repository;
    private final PolicyService policyService;
    public CancelPolicyService(CancelPolicyRepository repository, PolicyService policyService) {
        this.repository = repository; this.policyService = policyService;
    }
    public CancelPolicyResponse create(UUID policyId, CreateCancelPolicyRequest request) {
        Policy policy = policyService.findByIdAndType(policyId, PolicyType.CANCEL);
        if (repository.existsByPolicy_IdAndDaysBefore(policyId, request.daysBefore()))
            throw new DuplicateResourceException("Cancellation rule already exists for this day threshold");
        CancelPolicy rule = new CancelPolicy();
        rule.setPolicy(policy); rule.setDaysBefore(request.daysBefore());
        rule.setRefundPercent(request.refundPercent()); rule.setStatus(PolicyStatus.ACTIVE);
        return toResponse(repository.save(rule));
    }
    @Transactional(readOnly = true)
    public List<CancelPolicyResponse> getAll(UUID policyId, boolean activeOnly) {
        policyService.findByIdAndType(policyId, PolicyType.CANCEL);
        List<CancelPolicy> rules = activeOnly
            ? repository.findAllByPolicy_IdAndStatusOrderByDaysBeforeDesc(policyId, PolicyStatus.ACTIVE)
            : repository.findAllByPolicy_IdOrderByDaysBeforeDesc(policyId);
        return rules.stream().map(this::toResponse).toList();
    }
    public CancelPolicyResponse update(UUID policyId, UUID id, UpdateCancelPolicyRequest request) {
        CancelPolicy rule = find(policyId, id);
        if (repository.existsByPolicy_IdAndDaysBeforeAndIdNot(policyId, request.daysBefore(), id))
            throw new DuplicateResourceException("Cancellation rule already exists for this day threshold");
        rule.setDaysBefore(request.daysBefore()); rule.setRefundPercent(request.refundPercent());
        rule.setStatus(request.status()); return toResponse(repository.save(rule));
    }
    public CancelPolicyResponse deactivate(UUID policyId, UUID id) {
        CancelPolicy rule = find(policyId, id); rule.setStatus(PolicyStatus.INACTIVE);
        return toResponse(repository.save(rule));
    }
    private CancelPolicy find(UUID policyId, UUID id) {
        return repository.findByIdAndPolicy_Id(id, policyId).orElseThrow(() ->
            new ResourceNotFoundException("Cancellation rule not found with id: " + id));
    }
    private CancelPolicyResponse toResponse(CancelPolicy r) {
        return new CancelPolicyResponse(r.getId(), r.getPolicy().getId(), r.getDaysBefore(), r.getRefundPercent(), r.getStatus());
    }
}
