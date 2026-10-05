package com.project.cruise.android.data.dto.onboard

import java.math.BigDecimal

data class ActivityCruiseTourResponse(
    val id: String,
    val tourId: String,
    val tourCode: String?,
    val tourName: String?,

    // Activity info
    val activityCruiseId: String?,
    val activityCruiseName: String?,
    val activityCruiseDescription: String?,
    val activityCruiseImageUrl: String?,

    // Cruise Area info
    val cruiseAreaId: String?,
    val cruiseAreaName: String?,

    // Configuration & Timings
    val startTime: String?,
    val endTime: String?,
    val maxPassengers: Int?,
    val price: BigDecimal?,
    val status: String?,

    val createdAt: String?,
    val updatedAt: String?
)