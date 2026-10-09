package com.project.booking.mapper;

import com.project.booking.client.ProductTourManagementClient.ProductUsageManagementInfo;
import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.model.ProductUsage;
import org.springframework.stereotype.Component;

@Component
public class ProductUsageMapper {

    public ProductUsageResponse toResponse(ProductUsage usage) {
        return new ProductUsageResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getProductTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }

    public ProductUsageManagementResponse toManagementResponse(
            ProductUsage usage,
            ProductUsageManagementInfo info) {

        return new ProductUsageManagementResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getProductTourId(),
                info != null ? info.tourId() : null,
                info != null ? info.productId() : null,
                info != null ? info.productName() : null,
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                info != null ? info.productTourStatus() : null,
                info != null && info.productActive(),
                usage.getUsedAt());
    }
}
