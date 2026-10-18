package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class RefundPayment(val id: Long, val referenceId: Long, val referenceType: String,
    val amount: BigDecimal, val status: String, val method: String)
data class PaymentRefundRequest(val amount: BigDecimal, val reason: String, val clientRequestId: String? = null)
data class PaymentRefundReceipt(val refundId: String, val paymentId: Long, val amount: BigDecimal, val status: String)
data class PaymentRefundItem(val id: String, val amount: BigDecimal, val reason: String, val status: String, val createdAt: String)
data class PaymentRefundSummary(val paymentId: Long, val paidAmount: BigDecimal, val refundedAmount: BigDecimal,
    val pendingAmount: BigDecimal, val refundableAmount: BigDecimal, val canRefund: Boolean,
    val blockedReason: String?, val refunds: List<PaymentRefundItem>, val page: Int, val size: Int)
