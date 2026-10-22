package com.project.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BillPaymentRequest(
                Long billId,
                UUID payerId,
                BigDecimal amount) {
}