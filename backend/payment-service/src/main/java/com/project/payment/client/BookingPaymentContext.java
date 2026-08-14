package com.project.payment.client;

import java.math.BigDecimal;

public record BookingPaymentContext(
    Long bookingId, Long userId, BigDecimal totalAmount, String status
) {}
