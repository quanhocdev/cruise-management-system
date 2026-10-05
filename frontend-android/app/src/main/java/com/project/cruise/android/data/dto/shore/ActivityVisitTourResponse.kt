package com.project.cruise.android.data.dto.shore

import java.math.BigDecimal

data class ActivityVisitTourResponse(
    val id: String,
    val tourId: String,
    val scheduleStopId: String?,
    val name: String?,
    val description: String?,
    val startTime: String?,
    val endTime: String?,
    val maxPassengers: Int?,
    val price: BigDecimal?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)