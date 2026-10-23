package com.project.booking.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record BillPaymentRequest(Long billId, UUID payerId, BigDecimal amount, UUID clientRequestId) {
    public BillPaymentRequest(Long billId, UUID payerId, BigDecimal amount) {
        this(billId, payerId, amount, null);
    }
}
