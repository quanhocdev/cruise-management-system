package com.project.cruise.android.data.repository.finance

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.repository.pos.OnlinePosCommands

interface PaymentRefundSource {
    suspend fun find(bookingCode: String): List<RefundPayment>
    suspend fun summary(paymentId: Long, page: Int): PaymentRefundSummary
    suspend fun refund(paymentId: Long, request: PaymentRefundRequest): PaymentRefundReceipt
}

class PaymentRefundRepository(context: Context) : PaymentRefundSource {
    // Đổi sang FinanceApiService vì các endpoint spending, payments/refunds thuộc phân hệ finance
    private val api = RetrofitClient.createFinanceApiService(TokenManager(context.applicationContext))
    private val commands = OnlinePosCommands(context.applicationContext)

    override suspend fun find(bookingCode: String): List<RefundPayment> {
        val booking = api.getFinanceSpending(bookingCode)
        return api.findRefundPayments(booking.bookingId, "BOOKING")
    }

    override suspend fun summary(paymentId: Long, page: Int) = api.getPaymentRefunds(paymentId, page)

    override suspend fun refund(paymentId: Long, request: PaymentRefundRequest) =
        commands.execute("PAYMENT_REFUND:$paymentId", request) { key ->
            api.refundPayment(paymentId, request.copy(clientRequestId = key))
        }
}