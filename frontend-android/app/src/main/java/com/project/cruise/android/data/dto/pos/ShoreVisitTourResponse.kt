package com.project.cruise.android.data.dto.pos
data class ShoreVisitTourResponse(
    val id: String,
    val tourId: String?,
    val scheduleStopId: String?,
    val name: String,
    val description: String?,
    val startTime: String?,
    val endTime: String?,
    val maxPassengers: Int?,
    val price: Double?,
    val status: String?
)