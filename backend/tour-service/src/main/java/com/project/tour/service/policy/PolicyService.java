package com.project.tour.service.policy;

import com.project.tour.dto.policy.CreatePolicyRequest;
import com.project.tour.dto.policy.PolicyResponse;
import com.project.tour.dto.policy.UpdatePolicyRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.policy.PolicyMapper;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.policy.PolicyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // CREATE
    public PolicyResponse create(CreatePolicyRequest request) {

        if (policyRepository.existsByType(request.getType())) {
            throw new AppException(
                    "Policy with type " + request.getType() + " already exists",
                    HttpStatus.CONFLICT);
        }

        Policy policy = PolicyMapper.toEntity(request);

        Policy savedPolicy = policyRepository.save(policy);

        return PolicyMapper.toResponse(savedPolicy);
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public PolicyResponse getById(UUID id) {

        Policy policy = findById(id);

        return PolicyMapper.toResponse(policy);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<PolicyResponse> getAll(
            PolicyType type,
            PolicyStatus status) {

        List<Policy> policies;

        if (type != null && status != null) {

            policies = policyRepository
                    .findAllByTypeAndStatusOrderByCreatedAtDesc(
                            type,
                            status);

        } else if (type != null) {

            policies = policyRepository
                    .findAllByTypeOrderByCreatedAtDesc(type);

        } else if (status != null) {

            policies = policyRepository
                    .findAllByStatusOrderByCreatedAtDesc(status);

        } else {

            policies = policyRepository
                    .findAllByOrderByCreatedAtDesc();
        }

        return policies.stream()
                .map(PolicyMapper::toResponse)
                .toList();
    }

    // UPDATE
    public PolicyResponse update(
            UUID id,
            UpdatePolicyRequest request) {

        Policy policy = findById(id);

        PolicyMapper.updateEntity(policy, request);

        Policy updatedPolicy = policyRepository.save(policy);

        return PolicyMapper.toResponse(updatedPolicy);
    }

    // SOFT DELETE
    public void deactivate(UUID id) {

        Policy policy = findById(id);

        policy.setStatus(PolicyStatus.INACTIVE);

        policyRepository.save(policy);
    }

    private Policy findById(UUID id) {

        return policyRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Policy not found with id: " + id,
                        HttpStatus.NOT_FOUND));
    }
}