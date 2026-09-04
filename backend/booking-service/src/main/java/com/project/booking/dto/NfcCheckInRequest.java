package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NfcCheckInRequest(
    @NotBlank(message = "NFC tag ID is required")
    @Size(max = 100, message = "NFC tag ID must not exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9:_-]+$", message = "NFC tag ID has an invalid format")
    String nfcTagId
) {}
