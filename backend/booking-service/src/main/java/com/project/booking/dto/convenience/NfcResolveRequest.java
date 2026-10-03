package com.project.booking.dto.convenience;

import jakarta.validation.constraints.NotBlank;

public record NfcResolveRequest(

        @NotBlank(message = "NFC card UID không được để trống") String nfcCardUid

) {
}