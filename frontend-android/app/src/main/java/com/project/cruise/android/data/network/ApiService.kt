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
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageRequest
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageRequest
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageRequest
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageResponse

import okhttp3.MultipartBody

interface ApiService {

    @GET("api/passengers/bookings/{id}/activities")
    suspend fun getRegistrationCatalog(@Path("id") id: Long): com.project.cruise.android.data.dto.booking.RegistrationCatalog
    @GET("api/passengers/bookings/{id}/registrations")
    suspend fun getRegistrations(@Path("id") id: Long,@Query("page") page: Int=0): List<com.project.cruise.android.data.dto.booking.ActivityRegistration>
    @POST("api/passengers/registrations")
    suspend fun registerActivity(@Body request: com.project.cruise.android.data.dto.booking.RegisterActivityRequest): com.project.cruise.android.data.dto.booking.ActivityRegistration
    @POST("api/passengers/registrations/{id}/cancel")
    suspend fun cancelRegistration(@Path("id") id: Long,@Body request: com.project.cruise.android.data.dto.booking.RegistrationCommand): com.project.cruise.android.data.dto.booking.ActivityRegistration
    @GET("api/{department}/{usage}/registrations")
    suspend fun getStaffRegistrations(@Path("department") department: String,@Path("usage") usage: String,@Query("bookingCode") bookingCode: String,@Query("search") search: String,@Query("page") page: Int): List<com.project.cruise.android.data.dto.booking.ActivityRegistration>
    @POST("api/{department}/{usage}/registrations/{id}/attend")
    suspend fun attendRegistration(@Path("department") department: String,@Path("usage") usage: String,@Path("id") id: Long,@Body request: com.project.cruise.android.data.dto.booking.RegistrationAttendance): com.project.cruise.android.data.dto.booking.ActivityRegistration
    @GET("api/v1/notifications/activities")
    suspend fun getActivityNotices(@Query("page") page: Int=0): List<com.project.cruise.android.data.dto.booking.ActivityNotice>

    @GET("api/v1/payments")
    suspend fun findRefundPayments(@Query("referenceId") referenceId: Long, @Query("referenceType") type: String): List<com.project.cruise.android.data.dto.booking.RefundPayment>
    @GET("api/v1/payments/{id}/refunds")
    suspend fun getPaymentRefunds(@Path("id") id: Long, @Query("page") page: Int = 0): com.project.cruise.android.data.dto.booking.PaymentRefundSummary
    @POST("api/v1/payments/{id}/refunds")
    suspend fun refundPayment(@Path("id") id: Long, @Body request: com.project.cruise.android.data.dto.booking.PaymentRefundRequest): com.project.cruise.android.data.dto.booking.PaymentRefundReceipt

    @GET("api/finance/bookings/ledger")
    suspend fun getFinanceLedger(@Query("bookingCode") code: String, @Query("page") page: Int): com.project.cruise.android.data.dto.booking.LedgerPage
    @GET("api/convenience/product-usages/ledger")
    suspend fun getConvenienceLedger(@Query("bookingCode") code: String, @Query("page") page: Int): com.project.cruise.android.data.dto.booking.LedgerPage
    @POST("api/finance/bookings/ledger/{id}/corrections")
    suspend fun correctFinanceLedger(@Path("id") id: Long, @Body request: com.project.cruise.android.data.dto.booking.LedgerCorrectionRequest): com.project.cruise.android.data.dto.booking.LedgerCorrectionResult
    @POST("api/convenience/product-usages/ledger/{id}/corrections")
    suspend fun correctConvenienceLedger(@Path("id") id: Long, @Body request: com.project.cruise.android.data.dto.booking.LedgerCorrectionRequest): com.project.cruise.android.data.dto.booking.LedgerCorrectionResult

    @GET("api/finance/bookings/spending-account")
    suspend fun getFinanceSpending(@Query("bookingCode") code: String): com.project.cruise.android.data.dto.booking.FinanceSpendingAccount

    @retrofit2.http.PUT("api/finance/bookings/{id}/spending-limit")
    suspend fun setSpendingLimit(@Path("id") id: Long, @Body request: com.project.cruise.android.data.dto.booking.SpendingLimitRequest): com.project.cruise.android.data.dto.booking.SpendingLimitResult

    @POST("api/finance/bookings/{id}/ledger-history")
    suspend fun importLedgerHistory(@Path("id") id: Long, @Body request: com.project.cruise.android.data.dto.booking.LedgerHistoryRequest): com.project.cruise.android.data.dto.booking.LedgerHistoryResult

    @GET("api/passengers/bookings/{id}/spending")
    suspend fun getBookingSpending(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): com.project.cruise.android.data.dto.booking.SpendingSummary


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

    @POST("api/shore/nfc/resolve")
    suspend fun resolveShoreNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/shore/visit-tours/tour/{tourId}")
    suspend fun getShoreActivityVisitTours(
        @Path("tourId") tourId: String
    ): List<ActivityVisitTourResponse>

    @POST("api/shore/activity-visit-usages")
    suspend fun createActivityVisitUsage(
        @Body request: ActivityVisitUsageRequest
    ): ActivityVisitUsageResponse

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
    @Multipart
    @POST("api/passengers/bookings")
    suspend fun createPassengerBooking(
        @Part parts: List<MultipartBody.Part>
    ): BookingResponse
}

