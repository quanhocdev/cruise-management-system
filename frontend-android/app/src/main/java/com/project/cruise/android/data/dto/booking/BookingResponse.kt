package com.project.cruise.android.data.dto.booking
import java.math.BigDecimal
data class BookingResponse(
    val id: Long,
    val createdByUserId: Long?,
    val primaryContactEmail: String?,
    val tourId: String?,
    val tourPackageId: String?,
    val bookingCode: String?,
    val numberPassengers: Int?,
    val primaryContactName: String?,
    val primaryContactPhone: String?,
    val totalAmount: BigDecimal?,
    val status: BookingStatus?,
    val bookingPassengers: List<BookingPassengerInfoResponse>?,
    val createdAt: String?,
    val updatedAt: String?
)