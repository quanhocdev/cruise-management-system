package com.project.booking.mapper;

import com.project.booking.dto.ProductUsageResponse;
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
}