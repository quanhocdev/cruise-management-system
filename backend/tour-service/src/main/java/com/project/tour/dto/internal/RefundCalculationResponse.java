package com.project.tour.dto.internal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RefundCalculationResponse(
        UUID policyRefundRuleId,
        LocalDate departureDate,
        Integer daysBeforeDeparture,
        BigDecimal refundPercentage,
        BigDecimal calculatedRefundAmount) {
}