package com.project.cruise.android.data.dto.booking

data class BookingPassengerResponse(
    val id: Long?,
    val passengerId: Long?,
    val bookingId: Long?,
    val roomId: String?,
    val checkinStatus: String?,
    val checkedInAt: String?
)