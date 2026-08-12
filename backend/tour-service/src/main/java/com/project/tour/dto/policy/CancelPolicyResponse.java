package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class CancelPolicyResponse {

    private UUID id;
    private UUID policyId;
    private Integer daysBefore;
    private BigDecimal refundPercent;
    private PolicyStatus status;

    public CancelPolicyResponse() {
    }

    public CancelPolicyResponse(
            UUID id,
            UUID policyId,
            Integer daysBefore,
            BigDecimal refundPercent,
            PolicyStatus status) {
        this.id = id;
        this.policyId = policyId;
        this.daysBefore = daysBefore;
        this.refundPercent = refundPercent;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public Integer getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(Integer daysBefore) {
        this.daysBefore = daysBefore;
    }

    public BigDecimal getRefundPercent() {
        return refundPercent;
    }

    public void setRefundPercent(BigDecimal refundPercent) {
        this.refundPercent = refundPercent;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }
}