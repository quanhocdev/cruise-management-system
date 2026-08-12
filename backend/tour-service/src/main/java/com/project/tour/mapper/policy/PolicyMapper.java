package com.project.tour.mapper.policy;

import com.project.tour.dto.policy.CreatePolicyRequest;
import com.project.tour.dto.policy.PolicyResponse;
import com.project.tour.dto.policy.UpdatePolicyRequest;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;

public class PolicyMapper {

    private PolicyMapper() {
    }

    public static Policy toEntity(CreatePolicyRequest request) {

        Policy policy = new Policy();

        policy.setType(request.getType());
        policy.setTitle(request.getTitle().trim());
        policy.setContent(request.getContent().trim());
        policy.setStatus(PolicyStatus.ACTIVE);

        return policy;
    }

    public static void updateEntity(
            Policy policy,
            UpdatePolicyRequest request) {

        policy.setTitle(request.getTitle().trim());
        policy.setContent(request.getContent().trim());
        policy.setStatus(request.getStatus());
    }

    public static PolicyResponse toResponse(Policy policy) {

        PolicyResponse response = new PolicyResponse();

        response.setId(policy.getId());
        response.setType(policy.getType());
        response.setTitle(policy.getTitle());
        response.setContent(policy.getContent());
        response.setStatus(policy.getStatus());
        response.setCreatedAt(policy.getCreatedAt());
        response.setUpdatedAt(policy.getUpdatedAt());

        return response;
    }
}