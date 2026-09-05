package com.project.cruise.android.data.network

import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RefreshResponse
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.UserInfoResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import com.project.cruise.android.data.dto.passenger.AvailableRoom
import com.project.cruise.android.data.dto.passenger.Departure
import com.project.cruise.android.data.dto.passenger.TourDetail
import com.project.cruise.android.data.dto.passenger.TourSummary
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/bookings")
    suspend fun createPassengerBooking(
        @Body request: com.project.cruise.android.data.dto.passenger.CreateBookingRequest
    ): com.project.cruise.android.data.dto.passenger.PassengerBookingResponse

    @GET("api/passenger/tours")
    suspend fun getOpenTours(): List<TourSummary>

    @GET("api/passenger/tours/{tourId}")
    suspend fun getTourDetail(@retrofit2.http.Path("tourId") tourId: String): TourDetail

    @GET("api/passenger/tours/{tourId}/departures")
    suspend fun getDepartures(@retrofit2.http.Path("tourId") tourId: String): List<Departure>

    @GET("api/v1/bookings/voyages/{voyageId}/available-rooms")
    suspend fun getAvailableRooms(@retrofit2.http.Path("voyageId") voyageId: String): List<AvailableRoom>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): JwtResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyOtpRequest
    ): Map<String, String>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): UserInfoResponse

    @POST("api/auth/refresh")
    fun refresh(
        @Header("Authorization") authorization: String
    ): Call<RefreshResponse>
}
