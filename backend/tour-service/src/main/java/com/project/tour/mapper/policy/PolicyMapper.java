package com.project.tour.mapper.policy;

import com.project.tour.dto.policy.CreatePolicyRequest;
import com.project.tour.dto.policy.PolicyResponse;
import com.project.tour.dto.policy.UpdatePolicyRequest;
import com.project.tour.model.Policy;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public Policy toEntity(CreatePolicyRequest request) {

        Policy policy = new Policy();

        policy.setType(request.getType());
        policy.setTitle(request.getTitle());
        policy.setContent(request.getContent());

        return policy;
    }

    public void updateEntity(
            Policy policy,
            UpdatePolicyRequest request) {

        policy.setType(request.getType());
        policy.setTitle(request.getTitle());
        policy.setContent(request.getContent());
        policy.setStatus(request.getStatus());
    }

    public PolicyResponse toResponse(Policy policy) {

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