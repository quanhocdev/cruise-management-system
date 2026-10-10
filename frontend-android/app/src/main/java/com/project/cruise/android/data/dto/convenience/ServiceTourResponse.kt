package com.project.cruise.android.data.dto.convenience

data class ServiceTourResponse(
    val id: String,
    val tourId: String,
    val tourCode: String?,
    val tourName: String?,
    val serviceId: String,
    val serviceName: String?,
    val serviceDescription: String?,
    val servicePrice: String?,
    val serviceImageUrl: String?,
    val cruiseAreaId: String?,
    val cruiseAreaName: String?,
    val cruiseDeckId: String?,
    val deckNumber: Int?,
    val maxPassengers: Int?,
    val durationMinutes: Int?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)