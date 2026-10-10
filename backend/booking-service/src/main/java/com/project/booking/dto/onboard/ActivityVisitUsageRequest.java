package com.project.booking.dto.onboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActivityVisitUsageRequest(

                @NotBlank(message = "NFC card UID không được để trống") String nfcCardUid,

                @NotNull(message = "Visit tour ID không được để trống") UUID visitTourId) {
}