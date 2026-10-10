package com.project.tour.dto.internal;

import com.project.tour.model.convenience.enums.ProductTourStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUsageManagementInfo(
        UUID productTourId,
        UUID tourId,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        ProductTourStatus productTourStatus,
        boolean productActive) {
}