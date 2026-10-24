package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient

interface PaymentRefundSource {
    suspend fun find(bookingCode: String): List<RefundPayment>
    suspend fun summary(paymentId: Long, page: Int): PaymentRefundSummary
    suspend fun refund(paymentId: Long, request: PaymentRefundRequest): PaymentRefundReceipt
}

class PaymentRefundRepository(context: Context): PaymentRefundSource {
    private val api = RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands = OnlinePosCommands(context.applicationContext)
    override suspend fun find(bookingCode: String): List<RefundPayment> {
        val booking = api.getFinanceSpending(bookingCode)
        return api.findRefundPayments(booking.bookingId,"BOOKING")
    }
    override suspend fun summary(paymentId: Long,page: Int) = api.getPaymentRefunds(paymentId,page)
    override suspend fun refund(paymentId: Long,request: PaymentRefundRequest) =
        commands.execute("PAYMENT_REFUND:$paymentId",request) { key ->
            api.refundPayment(paymentId,request.copy(clientRequestId=key))
        }
}
