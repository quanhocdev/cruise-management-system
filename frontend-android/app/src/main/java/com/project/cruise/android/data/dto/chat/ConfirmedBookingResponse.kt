package com.project.cruise.android.data.dto.chat

import java.math.BigDecimal

data class ConfirmedBookingResponse(
    val bookingId: Long,
    val bookingCode: String?,
    val recipientName: String?,
    val numberPassengers: Int?,
    val totalAmount: BigDecimal?,
    val confirmedAt: String?
)