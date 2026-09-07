package com.project.cruise.android.data.dto.passenger

import java.math.BigDecimal

data class BookingTripDetails(
    val voyageId: String,
    val tourName: String?,
    val cruiseName: String?,
    val startDate: String?,
    val endDate: String?,
    val rooms: List<BookedRoomDetails> = emptyList(),
    val itinerary: List<ItineraryDay>? = null
)

data class BookedRoomDetails(
    val roomId: String,
    val roomCode: String?,
    val deckNumber: Int?,
    val roomTypeName: String?
)

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
    val bookingCode: String?,
    val createdByUserId: Long? = null,
    val primaryContactName: String,
    val primaryContactPhone: String,
    val totalAmount: BigDecimal,
    val status: String,
    val paymentId: Long? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val passengers: List<PassengerVoyageBookingResponse> = emptyList()
)

data class PassengerVoyageBookingResponse(
    val passengerVoyageId: Long,
    val passengerId: Long,
    val userId: Long? = null,
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val cabinId: String? = null,
    val passengerStatus: String,
    val embarkationStatus: String,
    val nfcTagId: String? = null,
    val checkedInAt: String? = null,
    val boardedAt: String? = null,
    val disembarkedAt: String? = null
)

data class CreateVnPayPaymentRequest(
    val referenceId: Long,
    val referenceType: String = "BOOKING",
    val amount: BigDecimal,
    val method: String = "VNPAY"
)

data class PassengerPaymentResponse(
    val id: Long,
    val referenceId: Long,
    val amount: BigDecimal,
    val method: String,
    val status: String,
    val paymentUrl: String?
)
