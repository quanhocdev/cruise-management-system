
package com.project.tour.dto.response.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record PolicyRefundRuleResponse(

        UUID id,
        UUID policyId,
        Integer minDaysBeforeDeparture,
        Integer maxDaysBeforeDeparture,
        BigDecimal refundPercentage,
        String description

) {
}
