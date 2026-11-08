package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.booking.ActivityRegistration
import com.project.cruise.android.data.dto.booking.BookingPayment
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.booking.RegisterActivityRequest
import com.project.cruise.android.data.dto.booking.RegistrationCatalog
import com.project.cruise.android.data.dto.booking.RegistrationCommand
import com.project.cruise.android.data.dto.booking.SpendingSummary
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApiService {
    @GET("api/passenger/payments/booking/{id}")
    suspend fun getBookingPayment(@Path("id") id: Long): BookingPayment

    @GET("api/passengers/bookings/{id}/qr")
    suspend fun getBookingQr(@Path("id") id: Long): ResponseBody

    @GET("api/passengers/bookings")
    suspend fun getMyBookings(): List<BookingResponse>

    @GET("api/passengers/bookings/{id}")
    suspend fun getBookingById(
        @Path("id") id: Long,
        @Query("privileged") privileged: Boolean = false
    ): BookingResponse

    @Multipart
    @POST("api/passengers/bookings")
    suspend fun createPassengerBooking(
        @Part parts: List<MultipartBody.Part>
    ): BookingResponse

    @GET("api/passengers/bookings/{id}/activities")
    suspend fun getRegistrationCatalog(@Path("id") id: Long): RegistrationCatalog

    @GET("api/passengers/bookings/{id}/registrations")
    suspend fun getRegistrations(
        @Path("id") id: Long,
        @Query("page") page: Int = 0
    ): List<ActivityRegistration>

    @POST("api/passengers/registrations")
    suspend fun registerActivity(
        @Body request: RegisterActivityRequest
    ): ActivityRegistration

    @POST("api/passengers/registrations/{id}/cancel")
    suspend fun cancelRegistration(
        @Path("id") id: Long,
        @Body request: RegistrationCommand
    ): ActivityRegistration

    @GET("api/passengers/bookings/{id}/spending")
    suspend fun getBookingSpending(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): SpendingSummary
}