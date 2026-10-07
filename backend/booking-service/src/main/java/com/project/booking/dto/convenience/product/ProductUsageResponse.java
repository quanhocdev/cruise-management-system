package com.project.booking.dto.convenience.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductUsageResponse(

                Long id,

                Long bookingPassengerId,

                UUID productTourId,

                Integer quantity,

                BigDecimal unitPrice,

                BigDecimal discountAmount,

                BigDecimal finalAmount,

                LocalDateTime usedAt

) {
}