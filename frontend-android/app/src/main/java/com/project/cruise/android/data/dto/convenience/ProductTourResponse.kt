package com.project.cruise.android.data.dto.convenience

data class ProductTourResponse(
    val id: String,
    val tourId: String,
    val tourCode: String?,
    val tourName: String?,
    val productId: String,
    val productName: String?,
    val productDescription: String?,
    val productImageUrl: String?,
    val cruiseAreaId: String?,
    val cruiseAreaName: String?,
    val cruiseDeckId: String?,
    val deckNumber: Int?,
    val quantity: Int?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)