
package com.project.booking.dto.passenger.bill;

import com.project.booking.model.enums.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PassengerBillDTO(
        Long id,
        Long bookingId,
        BigDecimal totalAmount,
        BillStatus status,
        List<PassengerBillItemDTO> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
