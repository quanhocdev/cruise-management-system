package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateBookingPolicyRequest {

    @NotNull(message = "Days before departure is required")
    @Min(value = 0, message = "Days before departure must be non-negative")
    private Integer daysBeforeDeparture;

    @NotNull(message = "Discount percent is required")
    @DecimalMin(value = "0.00", message = "Discount percent must be at least 0")
    @DecimalMax(value = "100.00", message = "Discount percent must not exceed 100")
    private BigDecimal discountPercent;

    @NotNull(message = "Policy status is required")
    private PolicyStatus status;

    public Integer getDaysBeforeDeparture() {
        return daysBeforeDeparture;
    }

    public void setDaysBeforeDeparture(Integer daysBeforeDeparture) {
        this.daysBeforeDeparture = daysBeforeDeparture;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }
}