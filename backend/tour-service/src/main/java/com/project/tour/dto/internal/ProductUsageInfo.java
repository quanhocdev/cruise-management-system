package com.project.tour.dto.internal;

import com.project.tour.model.convenience.enums.ProductTourStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUsageInfo(
        UUID productTourId,
        UUID tourId,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        ProductTourStatus productTourStatus,
        boolean productActive,
        UUID packageBenefitId,
        Integer benefitQuantity,
        BigDecimal discountPercent) {
}