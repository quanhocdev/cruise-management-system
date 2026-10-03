package com.project.cruise.android.data.dto.convenience

data class ServiceUsageResponse(
    val id: Long,
    val bookingPassengerId: Long,
    val serviceTourId: String,
    val unitPrice: String?,
    val discountAmount: String?,
    val finalAmount: String?,
    val usedAt: String?,
    val expiresAt: String?,
    val endedAt: String?
)