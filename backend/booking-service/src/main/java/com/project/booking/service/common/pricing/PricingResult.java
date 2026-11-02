
package com.project.booking.service.common.pricing;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal unitPrice,
        long quantity,
        BigDecimal grossAmount,
        long discountedQuantity,
        BigDecimal discountAmount,
        BigDecimal finalAmount) {
}
