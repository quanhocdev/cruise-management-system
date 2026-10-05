package com.project.cruise.android.data.dto.onboard

import java.math.BigDecimal

data class ActivityCruiseUsageResponse(
    val id: Long,
    val bookingPassengerId: Long,
    val activityCruiseTourId: String,
    val quantity: Int,
    val unitPrice: BigDecimal?,
    val discountAmount: BigDecimal?,
    val finalAmount: BigDecimal?,
    val usedAt: String?
)