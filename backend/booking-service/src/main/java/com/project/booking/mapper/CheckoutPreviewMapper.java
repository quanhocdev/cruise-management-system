package com.project.booking.mapper;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;
import org.springframework.stereotype.Component;

@Component
public class CheckoutPreviewMapper {

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ActivityVisitUsage usage) {

        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ActivityCruiseUsage usage) {

        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ServiceUsage usage) {

        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                null,
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }

    public CheckoutPreviewResponse.UsageItem toUsageItem(
            ProductUsage usage) {

        return new CheckoutPreviewResponse.UsageItem(
                usage.getId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }
}