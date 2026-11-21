package com.project.cruise.android.data.dto.passenger.tripdetail

import com.google.gson.annotations.SerializedName


// Khớp JSON trả về từ GET /api/passengers/bookings/tours
data class BookedTourItemDto(
    val tourId: String? = null,
    val bookingId: Long? = null,
    val tourCode: String? = null,
    val tourName: String? = null,
    val departureDate: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val statusTrip: String? = null,
    val departurePortName: String? = null,
    val arrivalPortName: String? = null,
    val cruiseName: String? = null,
    val imageUrl: String? = null
) {
    val resolvedStartDate: String?
        get() = departureDate ?: startDate
}


data class TripRouteOverviewDto(
    val tourId: String? = null,
    val tourCode: String? = null,
    val tourName: String? = null,
    val statusTrip: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    @SerializedName(value = "departurePortName", alternate = ["originPortName"])
    val departurePortName: String? = null,
    @SerializedName(value = "destinationPortName", alternate = ["arrivalPortName"])
    val destinationPortName: String? = null,
    val departurePort: TripPortDto? = null,
    val arrivalPort: TripPortDto? = null,
    val distanceKm: Double? = null,
    val routeGeoJson: com.google.gson.JsonElement? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null
) {
    val resolvedDepartureName: String?
        get() = departurePortName ?: departurePort?.name

    val resolvedDestinationName: String?
        get() = destinationPortName ?: arrivalPort?.name
}

data class TripRouteLegDto(
    @SerializedName(value = "legId", alternate = ["id"])
    val legId: String? = null,
    val legOrder: Int? = null,
    val fromPortName: String? = null,
    val toPortName: String? = null,
    val fromPort: TripPortDto? = null,
    val toPort: TripPortDto? = null,
    val distanceKm: Double? = null,
    val routeGeoJson: com.google.gson.JsonElement? = null,
    val departureTime: String? = null,
    val arrivalTime: String? = null,
    val status: String? = null
) {
    val resolvedKey: String
        get() = legId ?: "leg-${legOrder ?: 0}"

    val resolvedFromName: String
        get() = fromPortName ?: fromPort?.name ?: "Chưa xác định"

    val resolvedToName: String
        get() = toPortName ?: toPort?.name ?: "Chưa xác định"
}

data class TripPortDto(
    val id: String? = null,
    val code: String? = null,
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)


data class VesselLocationDto(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val updatedAt: String? = null,
    val timestamp: String? = null
)

data class TripScheduleDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val dayNumber: Int? = null,
    val realDay: String? = null,
    val stops: List<TripScheduleStopDto>? = emptyList()
)

data class TripScheduleStopDto(
    val id: String? = null,
    val stopOrder: Int? = null,
    val arriveAt: String? = null,
    val leaveAt: String? = null,
    val portName: String? = null,
    val portCity: String? = null,
    val portCountry: String? = null,
    val portDescription: String? = null,
    val visitActivity: TripVisitActivityDto? = null
)

data class TripVisitActivityDto(
    val id: String? = null,
    val visitName: String? = null,
    val name: String? = null,
    val visitDescription: String? = null,
    val description: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val price: Double? = null,
    val maxPassengers: Int? = null
)

data class TripCruiseDto(
    val id: String? = null,
    val name: String? = null,
    val code: String? = null,
    val description: String? = null,
    val maxPassengers: Int? = null,
    val imageUrl: String? = null
)

data class TripCruiseDeckDto(
    val id: String? = null,
    val deckNumber: Int? = null,
    val name: String? = null,
    val description: String? = null
)

data class TripCruiseDeckAreaDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val areaType: String? = null
)

data class TripProductDto(
    val id: String? = null,
    val productId: String? = null,
    val productName: String? = null,
    val name: String? = null,
    val productDescription: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val imageUrl: String? = null
)

data class TripServiceDto(
    val id: String? = null,
    val serviceId: String? = null,
    val serviceName: String? = null,
    val name: String? = null,
    val serviceDescription: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val maxPassengers: Int? = null,
    val durationMinutes: Int? = null,
    val imageUrl: String? = null
)

data class TripOnboardActivityDto(
    val id: String? = null,
    val activityId: String? = null,
    val activityName: String? = null,
    val name: String? = null,
    val activityDescription: String? = null,
    val description: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val maxPassengers: Int? = null,
    val price: Double? = null,
    val imageUrl: String? = null
)

data class TripPackageBenefitDto(
    val id: String? = null,
    val type: String? = null,
    val referenceId: String? = null,
    val quantity: Int? = null,
    val discountPercent: Double? = null,
    val benefitName: String? = null,
    val description: String? = null
)

data class TripPackageDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val maxPassengers: Int? = null,
    val benefits: List<TripPackageBenefitDto>? = emptyList()
)

// Khớp JSON thực tế của đơn đặt (booking-service)
data class PassengerBookingDetailDto(
    val id: Long? = null,
    val createdByUserId: String? = null,
    val tourId: String? = null,
    val tourPackageId: String? = null,
    val packageName: String? = null,
    val bookingCode: String? = null,
    val numberPassengers: Int? = null,
    val primaryContactName: String? = null,
    val primaryContactEmail: String? = null,
    val primaryContactPhone: String? = null,
    val numberOfRooms: Int? = null,
    val totalAmount: Double? = null,
    val status: String? = null,
    val paymentStatus: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// Khớp JSON thực tế của từng item trong danh sách hành khách
data class BookingPassengerItemDto(
    val id: Long? = null,
    val passenger: PassengerProfileDto? = null,
    val roomId: String? = null,
    val nfcCardUid: String? = null,
    val status: String? = null,
    val checkedInAt: String? = null,
    val checkedOutAt: String? = null
)

data class PassengerProfileDto(
    val id: Long? = null,
    val fullName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val idCardType: String? = null,
    val identificationNumber: String? = null,
    val documentNote: String? = null,
    val idCardImageUrl: String? = null
)

data class ResolvedBenefitUiModel(
    val id: String,
    val name: String,
    val description: String,
    val type: String?,
    val referenceId: String?,
    val quantity: Int?,
    val discountPercent: Double?
)

data class TripUsageItemDto(
    val id: String? = null,
    val itemName: String? = null,
    val name: String? = null,
    val quantity: Int? = null,
    val usedAt: String? = null,
    val status: String? = null,
    @SerializedName("totalPrice") val totalPrice: Double? = null
)

data class TripBillDto(
    val id: String? = null,
    val billCode: String? = null,
    val code: String? = null,
    val totalAmount: Double? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val paidAt: String? = null
)

data class TripWeatherDto(
    val temperature: Double? = null,
    val weatherCode: Int? = null,
    val description: String? = null,
    val windSpeed: Double? = null
)

fun resolveBookingPackageBenefits(
    booking: PassengerBookingDetailDto?,
    packages: List<TripPackageDto>,
    products: List<TripProductDto>,
    services: List<TripServiceDto>,
    cruiseActivities: List<TripOnboardActivityDto>,
    visits: List<TripVisitActivityDto>
): Pair<TripPackageDto?, List<ResolvedBenefitUiModel>> {
    val targetPackageId = booking?.tourPackageId ?: return null to emptyList()
    val matchedPackage = packages.firstOrNull { it.id == targetPackageId } ?: return null to emptyList()

    val lookup = mutableMapOf<String, Pair<String, String>>()
    products.forEach { p ->
        val id = p.id ?: p.productId
        if (!id.isNullOrBlank()) {
            lookup[id] = (p.productName ?: p.name ?: "Sản phẩm") to (p.productDescription ?: p.description ?: "")
        }
    }
    services.forEach { s ->
        val id = s.id ?: s.serviceId
        if (!id.isNullOrBlank()) {
            lookup[id] = (s.serviceName ?: s.name ?: "Dịch vụ") to (s.serviceDescription ?: s.description ?: "")
        }
    }
    cruiseActivities.forEach { a ->
        val id = a.id ?: a.activityId
        if (!id.isNullOrBlank()) {
            lookup[id] = (a.activityName ?: a.name ?: "Hoạt động trên tàu") to (a.activityDescription ?: a.description ?: "")
        }
    }
    visits.forEach { v ->
        val id = v.id
        if (!id.isNullOrBlank()) {
            lookup[id] = (v.visitName ?: v.name ?: "Điểm tham quan") to (v.visitDescription ?: v.description ?: "")
        }
    }

    val resolvedList = matchedPackage.benefits.orEmpty().mapIndexed { index, benefit ->
        val refInfo = benefit.referenceId?.let { lookup[it] }
        val descParts = buildList {
            val baseDesc = benefit.description?.takeIf { it.isNotBlank() } ?: refInfo?.second.orEmpty()
            if (baseDesc.isNotBlank()) add(baseDesc)
            if ((benefit.discountPercent ?: 0.0) > 0.0) {
                add("Ưu đãi giảm ${benefit.discountPercent}%")
            }
        }
        ResolvedBenefitUiModel(
            id = benefit.id ?: "${matchedPackage.id ?: "pkg"}-$index",
            name = benefit.benefitName?.takeIf { it.isNotBlank() }
                ?: refInfo?.first
                ?: benefit.type?.let { "Quyền lợi ($it)" }
                ?: "Quyền lợi #${index + 1}",
            description = descParts.joinToString(" • "),
            type = benefit.type,
            referenceId = benefit.referenceId,
            quantity = benefit.quantity,
            discountPercent = benefit.discountPercent
        )
    }

    return matchedPackage to resolvedList
}