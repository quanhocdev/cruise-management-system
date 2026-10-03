package com.project.cruise.android.data.dto.convenience

data class ProductUsageResponse(
    val id: Long,
    val bookingPassengerId: Long,
    val productTourId: String,
    val quantity: Int,
    val unitPrice: String?,
    val discountAmount: String?,
    val finalAmount: String?,
    val usedAt: String?
)