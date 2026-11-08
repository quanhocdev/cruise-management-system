package com.project.cruise.android.data.dto.pos

data class ConvenienceProductResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val stockQuantity: Int?,
    val imageUrl: String?,
    val status: String?
)