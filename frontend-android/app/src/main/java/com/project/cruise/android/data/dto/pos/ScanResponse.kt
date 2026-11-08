package com.project.cruise.android.data.dto.pos

data class ScanResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Long?
)
