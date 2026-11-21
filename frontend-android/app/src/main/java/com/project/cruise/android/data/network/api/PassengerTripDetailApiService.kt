package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.passenger.tripdetail.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PassengerTripDetailApiService {

    @GET("api/passengers/bookings/tours")
    suspend fun getMyBookedTours(): List<BookedTourItemDto>

    // ==========================================
    // TOUR SERVICE - ROUTE
    // ==========================================
    @GET("api/tours/routes/tours/{tourId}/overview")
    suspend fun getRouteOverview(
        @Path("tourId") tourId: String
    ): TripRouteOverviewDto

    @GET("api/tours/routes/tours/{tourId}/legs")
    suspend fun getRouteLegs(
        @Path("tourId") tourId: String
    ): List<TripRouteLegDto>

    // ==========================================
    // TOUR SERVICE - TRIP INFORMATION
    // ==========================================
    @GET("api/passenger/tours/{tourId}/itinerary")
    suspend fun getItinerary(
        @Path("tourId") tourId: String
    ): List<TripScheduleDto>

    @GET("api/passenger/tours/{tourId}/cruise")
    suspend fun getCruise(
        @Path("tourId") tourId: String
    ): TripCruiseDto

    @GET("api/passenger/tours/{tourId}/cruise/decks")
    suspend fun getCruiseDecks(
        @Path("tourId") tourId: String
    ): List<TripCruiseDeckDto>

    @GET("api/passenger/tours/{tourId}/cruise/decks/{deckId}/areas")
    suspend fun getCruiseDeckAreas(
        @Path("tourId") tourId: String,
        @Path("deckId") deckId: String
    ): List<TripCruiseDeckAreaDto>

    @GET("api/passenger/tours/{tourId}/products")
    suspend fun getProducts(
        @Path("tourId") tourId: String
    ): List<TripProductDto>

    @GET("api/passenger/tours/{tourId}/services")
    suspend fun getServices(
        @Path("tourId") tourId: String
    ): List<TripServiceDto>

    @GET("api/passenger/tours/{tourId}/activities-cruise")
    suspend fun getCruiseActivities(
        @Path("tourId") tourId: String
    ): List<TripOnboardActivityDto>

    @GET("api/passenger/tours/{tourId}/visits")
    suspend fun getVisits(
        @Path("tourId") tourId: String
    ): List<TripVisitActivityDto>

    @GET("api/passenger/tours/{tourId}/packages")
    suspend fun getPackages(
        @Path("tourId") tourId: String
    ): List<TripPackageDto>

    // ==========================================
    // BOOKING SERVICE
    // ==========================================
    @GET("api/passengers/bookings/{bookingId}/trip-detail")
    suspend fun getBookingTripDetail(
        @Path("bookingId") bookingId: Long
    ): PassengerBookingDetailDto

    @GET("api/passengers/bookings/{bookingId}/passengers")
    suspend fun getBookingPassengers(
        @Path("bookingId") bookingId: Long
    ): List<BookingPassengerItemDto>

    @GET("api/passengers/bookings/{bookingId}/usages/products")
    suspend fun getProductUsages(
        @Path("bookingId") bookingId: Long
    ): List<TripUsageItemDto>

    @GET("api/passengers/bookings/{bookingId}/usages/services")
    suspend fun getServiceUsages(
        @Path("bookingId") bookingId: Long
    ): List<TripUsageItemDto>

    @GET("api/passengers/bookings/{bookingId}/usages/activities-cruise")
    suspend fun getCruiseActivityUsages(
        @Path("bookingId") bookingId: Long
    ): List<TripUsageItemDto>

    @GET("api/passengers/bookings/{bookingId}/usages/visits")
    suspend fun getVisitUsages(
        @Path("bookingId") bookingId: Long
    ): List<TripUsageItemDto>

    @GET("api/passengers/bookings/{bookingId}/bills")
    suspend fun getBills(
        @Path("bookingId") bookingId: Long
    ): List<TripBillDto>

    // ==========================================
    // SHARED APIs (GPS POLLING 10s + WEATHER)
    // ==========================================
    @GET("api/vessel-tracking/tours/{tourId}/location")
    suspend fun getVesselLocation(
        @Path("tourId") tourId: String
    ): Response<VesselLocationDto>

    @GET("api/public/weather")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): TripWeatherDto
}