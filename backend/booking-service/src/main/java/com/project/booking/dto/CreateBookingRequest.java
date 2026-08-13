package com.project.booking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateBookingRequest(
    @NotNull UUID scheduleId,
    @NotNull UUID roomId,
    @NotNull @Min(1) @Max(20) Integer guestCount,
    @NotNull @Positive @Digits(integer = 17, fraction = 0) BigDecimal totalAmount
) {}
