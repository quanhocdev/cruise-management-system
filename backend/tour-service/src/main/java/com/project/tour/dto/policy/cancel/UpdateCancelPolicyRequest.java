package com.project.tour.dto.policy.cancel;

import com.project.tour.model.enums.PolicyStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateCancelPolicyRequest {

    @NotNull(message = "Days before is required")
    @Min(value = 0, message = "Days before must be non-negative")
    private Integer daysBefore;

    @NotNull(message = "Refund percent is required")
    @DecimalMin(value = "0.00", message = "Refund percent must be at least 0")
    @DecimalMax(value = "100.00", message = "Refund percent must not exceed 100")
    private BigDecimal refundPercent;

    @NotNull(message = "Policy status is required")
    private PolicyStatus status;

    public UpdateCancelPolicyRequest() {
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