package com.project.cruise.android.data.dto.passenger

data class TourSummary(
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val cruiseId: String,
    val cruiseName: String,
    val cruiseImageUrl: String?
)

data class TourDetail(
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val bookingStart: String,
    val bookingEnd: String,
    val cruiseId: String,
    val cruiseName: String,
    val cruiseDescription: String?,
    val cruiseImageUrl: String?,
    val maxPassengers: Int,
    val itinerary: List<ItineraryDay>
)

data class ItineraryDay(
    val id: String,
    val dayNumber: Int,
    val date: String,
    val name: String,
    val description: String?
)

data class Departure(
    val voyageId: String,
    val tourId: String,
    val tourCode: String,
    val departureDate: String,
    val returnDate: String,
    val cruiseId: String,
    val cruiseName: String,
    val capacity: Int,
    val status: String
)

data class AvailableRoom(
    val roomId: String,
    val roomCode: String,
    val deckId: String,
    val deckNumber: Int,
    val roomTypeId: String,
    val roomTypeName: String,
    val roomTypeDescription: String?,
    val price: Double,
    val capacity: Int,
    val occupiedCapacity: Long,
    val remainingCapacity: Long,
    val available: Boolean
)
