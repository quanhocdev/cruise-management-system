package com.project.booking.dto;

import jakarta.validation.constraints.NotNull;

public record ConfirmBookingPaymentRequest(@NotNull Long paymentId) {}
