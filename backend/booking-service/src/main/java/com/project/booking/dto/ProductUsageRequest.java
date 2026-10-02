package com.project.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductUsageRequest(

        @NotBlank(message = "NFC card UID không được để trống") String nfcCardUid,

        @NotNull(message = "Product tour ID không được để trống") UUID productTourId,

        @NotNull(message = "Số lượng không được để trống") @Min(value = 1, message = "Số lượng phải lớn hơn 0") Integer quantity

) {
}