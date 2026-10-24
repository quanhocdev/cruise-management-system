package com.project.booking.mapper;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;
import org.springframework.stereotype.Component;

@Component
public class CheckoutPreviewMapper {

    private final com.project.booking.service.account.LedgerAdjustmentReader adjustments;
    public CheckoutPreviewMapper(com.project.booking.service.account.LedgerAdjustmentReader adjustments) { this.adjustments=adjustments; }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ActivityVisitUsage usage) {
        var adjustment = adjustments.amount(usage.getBookingPassenger().getId(), "SHORE", usage.getId());
        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount().add(adjustment),
                usage.getUsedAt(), adjustment);
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ActivityCruiseUsage usage) {
        var adjustment = adjustments.amount(usage.getBookingPassenger().getId(), "ACTIVITY", usage.getId());
        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount().add(adjustment),
                usage.getUsedAt(), adjustment);
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ServiceUsage usage) {
        var adjustment = adjustments.amount(usage.getBookingPassenger().getId(), "SERVICE", usage.getId());
        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                null,
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount().add(adjustment),
                usage.getUsedAt(), adjustment);
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ProductUsage usage) {
        var adjustment = adjustments.amount(usage.getBookingPassenger().getId(), "PRODUCT", usage.getId());
        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount().add(adjustment),
                usage.getUsedAt(), adjustment);
    }
}
