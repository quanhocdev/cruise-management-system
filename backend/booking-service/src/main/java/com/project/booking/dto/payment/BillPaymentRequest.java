package com.project.booking.dto.payment;

import java.math.BigDecimal;

public record BillPaymentRequest(
        Long billId,
        Long payerId,
        BigDecimal amount) {
}