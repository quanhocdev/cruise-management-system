package com.project.booking.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record CreateBookingRequest(
    @NotNull UUID voyageId,
    @NotBlank @Size(max = 150) String primaryContactName,
    @NotBlank @Size(max = 30) String primaryContactPhone,
    @NotNull @Positive @Digits(integer = 17, fraction = 0) BigDecimal totalAmount,
    @NotEmpty @Size(max = 20) List<@Valid CreatePassengerRequest> passengers
) {}
