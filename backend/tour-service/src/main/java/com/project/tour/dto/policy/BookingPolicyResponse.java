package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record BookingPolicyResponse(
    UUID id, UUID policyId, Integer daysBeforeDeparture,
    BigDecimal discountPercent, PolicyStatus status
) {}
