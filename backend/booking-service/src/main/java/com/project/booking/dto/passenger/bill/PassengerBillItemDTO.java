
package com.project.booking.dto.passenger.bill;

import java.math.BigDecimal;

public record PassengerBillItemDTO(
        Long id,
        String usageType,
        Long usageId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount) {
}
