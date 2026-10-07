package com.project.booking.dto.convenience.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ServiceUsageRequest(

                @NotBlank(message = "NFC card UID không được để trống") String nfcCardUid,

                @NotNull(message = "Service tour ID không được để trống") UUID serviceTourId

) {
}