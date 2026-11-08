package com.project.cruise.android.data.dto.pos

data class ConvenienceServiceResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val durationMinutes: Int?,
    val maxPassengers: Int?,
    val imageUrl: String?
)