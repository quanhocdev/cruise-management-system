package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.booking.BillPaymentCommand
import com.project.cruise.android.data.dto.booking.CheckoutConfirm
import com.project.cruise.android.data.dto.booking.CheckoutPreview
import com.project.cruise.android.data.dto.booking.CheckoutSnapshot
import com.project.cruise.android.data.dto.booking.DepartureManifest
import com.project.cruise.android.data.dto.booking.DepartureRequest
import com.project.cruise.android.data.dto.booking.FinanceReport
import com.project.cruise.android.data.dto.booking.FinanceSpendingAccount
import com.project.cruise.android.data.dto.booking.LedgerCorrectionRequest
import com.project.cruise.android.data.dto.booking.LedgerCorrectionResult
import com.project.cruise.android.data.dto.booking.LedgerHistoryRequest
import com.project.cruise.android.data.dto.booking.LedgerHistoryResult
import com.project.cruise.android.data.dto.booking.LedgerPage
import com.project.cruise.android.data.dto.booking.PaymentRefundReceipt
import com.project.cruise.android.data.dto.booking.PaymentRefundRequest
import com.project.cruise.android.data.dto.booking.PaymentRefundSummary
import com.project.cruise.android.data.dto.booking.RefundPayment
import com.project.cruise.android.data.dto.booking.ServiceBill
import com.project.cruise.android.data.dto.booking.ServiceBillPayment
import com.project.cruise.android.data.dto.booking.SpendingLimitRequest
import com.project.cruise.android.data.dto.booking.SpendingLimitResult
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FinanceApiService {
    @GET("api/{department}/bookings/{id}/bill")
    suspend fun getServiceBill(
        @Path("department") department: String,
        @Path("id") id: Long
    ): ServiceBill

    @GET("api/finance/bookings/{id}/checkout-preview")
    suspend fun getCheckoutPreview(
        @Path("id") id: Long
    ): CheckoutPreview

    @POST("api/finance/bookings/{id}/checkout")
    suspend fun confirmCheckout(
        @Path("id") id: Long,
        @Body body: CheckoutConfirm
    ): CheckoutSnapshot

    @POST("api/finance/bookings/{id}/bill/payment")
    suspend fun createBillPayment(
        @Path("id") id: Long,
        @Body body: BillPaymentCommand
    ): ServiceBillPayment

    @GET("api/{department}/bookings/{id}/bill/receipt")
    suspend fun getBillReceipt(
        @Path("department") department: String,
        @Path("id") id: Long
    ): ResponseBody

    @GET("api/v1/payments")
    suspend fun findRefundPayments(
        @Query("referenceId") referenceId: Long,
        @Query("referenceType") type: String
    ): List<RefundPayment>

    @GET("api/v1/payments/{id}/refunds")
    suspend fun getPaymentRefunds(
        @Path("id") id: Long,
        @Query("page") page: Int = 0
    ): PaymentRefundSummary

    @POST("api/v1/payments/{id}/refunds")
    suspend fun refundPayment(
        @Path("id") id: Long,
        @Body request: PaymentRefundRequest
    ): PaymentRefundReceipt

    @GET("api/finance/bookings/ledger")
    suspend fun getFinanceLedger(
        @Query("bookingCode") code: String,
        @Query("page") page: Int
    ): LedgerPage

    @GET("api/convenience/product-usages/ledger")
    suspend fun getConvenienceLedger(
        @Query("bookingCode") code: String,
        @Query("page") page: Int
    ): LedgerPage

    @POST("api/finance/bookings/ledger/{id}/corrections")
    suspend fun correctFinanceLedger(
        @Path("id") id: Long,
        @Body request: LedgerCorrectionRequest
    ): LedgerCorrectionResult

    @POST("api/convenience/product-usages/ledger/{id}/corrections")
    suspend fun correctConvenienceLedger(
        @Path("id") id: Long,
        @Body request: LedgerCorrectionRequest
    ): LedgerCorrectionResult

    @GET("api/finance/bookings/spending-account")
    suspend fun getFinanceSpending(
        @Query("bookingCode") code: String
    ): FinanceSpendingAccount

    @PUT("api/finance/bookings/{id}/spending-limit")
    suspend fun setSpendingLimit(
        @Path("id") id: Long,
        @Body request: SpendingLimitRequest
    ): SpendingLimitResult

    @POST("api/finance/bookings/{id}/ledger-history")
    suspend fun importLedgerHistory(
        @Path("id") id: Long,
        @Body request: LedgerHistoryRequest
    ): LedgerHistoryResult

    @GET("api/finance/bookings/reports/services")
    suspend fun getFinanceReport(
        @Query("tourId") tourId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String = "ALL",
        @Query("sort") sort: String = "newest"
    ): FinanceReport

    @GET("api/finance/bookings/{id}/departures")
    suspend fun getDepartures(
        @Path("id") id: Long
    ): DepartureManifest

    @POST("api/finance/bookings/{id}/departures/{passengerId}")
    suspend fun confirmDeparture(
        @Path("id") id: Long,
        @Path("passengerId") passengerId: Long,
        @Body request: DepartureRequest
    ): com.project.cruise.android.data.dto.booking.PassengerDeparture
}