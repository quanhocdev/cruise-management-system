package com.project.booking.dto.finance;

public record CheckoutConfirmRequest(
        @jakarta.validation.constraints.NotNull java.util.UUID clientRequestId,
        @jakarta.validation.constraints.NotBlank String previewVersion) {}
