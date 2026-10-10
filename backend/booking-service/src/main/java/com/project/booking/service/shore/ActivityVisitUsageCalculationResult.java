package com.project.booking.service.shore;

import java.math.BigDecimal;

public record ActivityVisitUsageCalculationResult(
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        long freeQuantity,
        long paidQuantity) {
}