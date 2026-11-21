package com.project.cruise.android.data.repository.passenger

import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.data.network.api.PassengerTripDetailApiService

class PassengerTripDetailRepository(
    private val api: PassengerTripDetailApiService
) {
    suspend fun getRouteOverview(tourId: String): TripRouteOverviewDto =
        api.getRouteOverview(tourId)

    suspend fun getRouteLegs(tourId: String): List<TripRouteLegDto> =
        api.getRouteLegs(tourId)

    suspend fun getItinerary(tourId: String): List<TripScheduleDto> =
        api.getItinerary(tourId)

    suspend fun getCruise(tourId: String): TripCruiseDto =
        api.getCruise(tourId)

    suspend fun getCruiseDecks(tourId: String): List<TripCruiseDeckDto> =
        api.getCruiseDecks(tourId)

    suspend fun getCruiseDeckAreas(tourId: String, deckId: String): List<TripCruiseDeckAreaDto> =
        api.getCruiseDeckAreas(tourId, deckId)

    suspend fun getProducts(tourId: String): List<TripProductDto> =
        api.getProducts(tourId)

    suspend fun getServices(tourId: String): List<TripServiceDto> =
        api.getServices(tourId)

    suspend fun getCruiseActivities(tourId: String): List<TripOnboardActivityDto> =
        api.getCruiseActivities(tourId)

    suspend fun getVisits(tourId: String): List<TripVisitActivityDto> =
        api.getVisits(tourId)

    suspend fun getPackages(tourId: String): List<TripPackageDto> =
        api.getPackages(tourId)

    suspend fun getBookingTripDetail(bookingId: Long): PassengerBookingDetailDto =
        api.getBookingTripDetail(bookingId)

    suspend fun getBookingPassengers(bookingId: Long): List<BookingPassengerItemDto> =
        api.getBookingPassengers(bookingId)

    suspend fun getProductUsages(bookingId: Long): List<TripUsageItemDto> =
        api.getProductUsages(bookingId)

    suspend fun getServiceUsages(bookingId: Long): List<TripUsageItemDto> =
        api.getServiceUsages(bookingId)

    suspend fun getCruiseActivityUsages(bookingId: Long): List<TripUsageItemDto> =
        api.getCruiseActivityUsages(bookingId)

    suspend fun getVisitUsages(bookingId: Long): List<TripUsageItemDto> =
        api.getVisitUsages(bookingId)

    suspend fun getBills(bookingId: Long): List<TripBillDto> =
        api.getBills(bookingId)

    // Backend trả 204 No Content khi chưa có tọa độ GPS trên Redis
    suspend fun getVesselLocation(tourId: String): VesselLocationDto? {
        val response = api.getVesselLocation(tourId)
        if (response.code() == 204) return null
        if (!response.isSuccessful) {
            throw IllegalStateException("Không thể lấy vị trí tàu (HTTP ${response.code()})")
        }
        return response.body()
    }

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        startDate: String? = null,
        endDate: String? = null
    ): TripWeatherDto = api.getWeather(latitude, longitude, startDate, endDate)
}