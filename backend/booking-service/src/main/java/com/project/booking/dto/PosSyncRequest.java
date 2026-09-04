package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record PosSyncRequest(
    @NotBlank @Pattern(regexp = "[0-9a-fA-F-]{36}") String localId,
    @NotBlank @Pattern(regexp = "QR|NFC") String scanType,
    @NotBlank @Size(max = 500) String scannedValue,
    @NotNull Instant createdAt
) {}
