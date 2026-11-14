package com.project.booking.dto.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FinanceRefundRejectRequest(

        @NotBlank(message = "Rejection reason is required") @Size(max = 2000, message = "Rejection reason must not exceed 2000 characters") String reason

) {
}