package com.project.cruise.android.data.dto.booking

data class BookingPassengerInfoResponse(
    val id: Long?,
    val fullName: String?,
    val gender: String?,
    val phoneNumber: String?,
    val email: String?,
    val roomId: String?,
    val checkinStatus: String?,
    val checkedInAt: String?
)