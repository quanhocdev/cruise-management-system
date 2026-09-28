package com.project.tour.mapper.admin.policy;

import com.project.tour.dto.admin.policy.cancel.CancelPolicyResponse;
import com.project.tour.dto.admin.policy.cancel.CreateCancelPolicyRequest;
import com.project.tour.dto.admin.policy.cancel.UpdateCancelPolicyRequest;
import com.project.tour.model.admin.CancelPolicy;
import com.project.tour.model.admin.Policy;

public class CancelPolicyMapper {

    public static CancelPolicy toEntity(
            CreateCancelPolicyRequest request,
            Policy policy) {
        CancelPolicy entity = new CancelPolicy();

        entity.setPolicy(policy);
        entity.setDaysBefore(request.getDaysBefore());
        entity.setRefundPercent(request.getRefundPercent());

        return entity;
    }

    public static void updateEntity(
            CancelPolicy entity,
            UpdateCancelPolicyRequest request) {
        entity.setDaysBefore(request.getDaysBefore());
        entity.setRefundPercent(request.getRefundPercent());
        entity.setStatus(request.getStatus());
    }

    public static CancelPolicyResponse toResponse(
            CancelPolicy entity) {
        return new CancelPolicyResponse(
                entity.getId(),
                entity.getPolicy().getId(),
                entity.getDaysBefore(),
                entity.getRefundPercent(),
                entity.getStatus());
    }

    private CancelPolicyMapper() {
    }
}