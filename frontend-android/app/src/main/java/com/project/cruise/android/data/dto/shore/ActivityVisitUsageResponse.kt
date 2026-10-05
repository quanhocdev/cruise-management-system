package com.project.cruise.android.data.dto.shore

import java.math.BigDecimal

data class ActivityVisitUsageResponse(
    val id: Long,
    val bookingPassengerId: Long,
    val visitTourId: String,
    val quantity: Int,
    val unitPrice: BigDecimal?,
    val discountAmount: BigDecimal?,
    val finalAmount: BigDecimal?,
    val usedAt: String?
)