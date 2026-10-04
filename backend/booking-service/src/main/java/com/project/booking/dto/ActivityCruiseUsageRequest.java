package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActivityCruiseUsageRequest(

        @NotBlank(message = "NFC card UID không được để trống") String nfcCardUid,

        @NotNull(message = "Activity cruise tour ID không được để trống") UUID activityCruiseTourId

) {
}