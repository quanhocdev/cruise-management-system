package com.project.tour.service;

import com.project.tour.dto.policy.*;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PolicyService {
    private final PolicyRepository repository;

    public PolicyService(PolicyRepository repository) { this.repository = repository; }

    public PolicyResponse create(CreatePolicyRequest request) {
        Policy policy = new Policy();
        policy.setType(request.type());
        policy.setTitle(request.title().trim());
        policy.setContent(request.content().trim());
        policy.setStatus(PolicyStatus.ACTIVE);
        return toResponse(repository.save(policy));
    }

    @Transactional(readOnly = true)
    public PolicyResponse get(UUID id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAll(PolicyType type, boolean activeOnly) {
        List<Policy> policies;
        if (type != null && activeOnly) {
            policies = repository.findAllByTypeAndStatusOrderByCreatedAtDesc(type, PolicyStatus.ACTIVE);
        } else if (activeOnly) {
            policies = repository.findAllByStatusOrderByCreatedAtDesc(PolicyStatus.ACTIVE);
        } else {
            policies = repository.findAllByOrderByCreatedAtDesc();
            if (type != null) policies = policies.stream().filter(p -> p.getType() == type).toList();
        }
        return policies.stream().map(this::toResponse).toList();
    }

    public PolicyResponse update(UUID id, UpdatePolicyRequest request) {
        Policy policy = find(id);
        policy.setType(request.type());
        policy.setTitle(request.title().trim());
        policy.setContent(request.content().trim());
        policy.setStatus(request.status());
        return toResponse(repository.save(policy));
    }

    public PolicyResponse deactivate(UUID id) {
        Policy policy = find(id);
        policy.setStatus(PolicyStatus.INACTIVE);
        return toResponse(repository.save(policy));
    }

    Policy findByIdAndType(UUID id, PolicyType type) {
        Policy policy = find(id);
        if (policy.getType() != type) {
            throw new IllegalArgumentException("Policy must have type " + type);
        }
        return policy;
    }

    private Policy find(UUID id) {
        return repository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Policy not found with id: " + id));
    }

    private PolicyResponse toResponse(Policy p) {
        return new PolicyResponse(p.getId(), p.getType(), p.getTitle(), p.getContent(),
            p.getStatus(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
