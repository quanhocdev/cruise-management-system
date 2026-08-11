package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record CancelPolicyResponse(
    UUID id, UUID policyId, Integer daysBefore,
    BigDecimal refundPercent, PolicyStatus status
) {}
