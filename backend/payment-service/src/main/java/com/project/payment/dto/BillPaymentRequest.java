package com.project.payment.dto;

import java.math.BigDecimal;

public record BillPaymentRequest(
        Long billId,
        Long payerId,
        BigDecimal amount) {
}