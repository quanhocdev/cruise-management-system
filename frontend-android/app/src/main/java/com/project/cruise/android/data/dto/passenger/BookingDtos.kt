package com.project.cruise.android.data.dto.passenger

import java.math.BigDecimal

data class CreateBookingRequest(
    val voyageId: String,
    val primaryContactName: String,
    val primaryContactPhone: String,
    val passengers: List<CreatePassengerRequest>
)

data class CreatePassengerRequest(
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val cabinId: String,
    val phoneNumber: String? = null,
    val email: String? = null
)

data class PassengerBookingResponse(
    val id: Long,
    val voyageId: String,
    val bookingCode: String,
    val primaryContactName: String,
    val primaryContactPhone: String,
    val totalAmount: BigDecimal,
    val status: String,
    val createdAt: String
)
