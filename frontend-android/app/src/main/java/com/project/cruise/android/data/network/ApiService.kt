package com.project.cruise.android.data.network

import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RefreshResponse
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.UserInfoResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageRequest
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse

import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageRequest
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageResponse


interface ApiService {


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


    @GET("api/public/tours")
    suspend fun getPublicTours(): List<PublicTourSummaryResponse>

    @GET("api/public/tours/{id}")
    suspend fun getPublicTourDetail(
        @Path("id") id: String
    ): PublicTourDetailResponse

    @GET("api/passengers/bookings")
    suspend fun getMyBookings(): List<BookingResponse>

    @GET("api/passengers/bookings/{id}")
    suspend fun getBookingById(
        @Path("id") id: Long,
        @Query("privileged") privileged: Boolean = false
    ): BookingResponse


    @POST("api/convenience/nfc/resolve")
    suspend fun resolveConvenienceNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/convenience/product-tours/tour/{tourId}")
    suspend fun getConvenienceProductTours(
        @Path("tourId") tourId: String
    ): List<ProductTourResponse>

    @GET("api/convenience/service-tours/tour/{tourId}")
    suspend fun getConvenienceServiceTours(
        @Path("tourId") tourId: String
    ): List<ServiceTourResponse>

    @POST("api/convenience/product-usages")
    suspend fun createProductUsage(
        @Body request: ProductUsageRequest
    ): ProductUsageResponse

    @POST("api/convenience/service-usages")
    suspend fun createServiceUsage(
        @Body request: ServiceUsageRequest
    ): ServiceUsageResponse

    @POST("api/onboard/nfc/resolve")
    suspend fun resolveOnboardNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/onboard/activity-cruise-tours/tour/{tourId}")
    suspend fun getOnboardActivityCruiseTours(
        @Path("tourId") tourId: String
    ): List<ActivityCruiseTourResponse>

    @POST("api/onboard/activity-cruise-usages")
    suspend fun createActivityCruiseUsage(
        @Body request: ActivityCruiseUsageRequest
    ): ActivityCruiseUsageResponse
}

