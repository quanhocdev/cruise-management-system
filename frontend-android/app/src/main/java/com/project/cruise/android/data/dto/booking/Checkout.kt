package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class CheckoutConfirm(val previewVersion: String,val clientRequestId: String?=null)
data class BillPaymentCommand(val clientRequestId: String)
data class CheckoutPreview(val bookingId: Long,val bookingCode: String,val primaryContactName: String?,
    val passengers: List<CheckoutGuest>,val grandTotal: BigDecimal,val previewVersion: String)
data class CheckoutGuest(val bookingPassengerId: Long,val passengerName: String?,
    val activityVisitUsages: List<CheckoutUsage>,val activityCruiseUsages: List<CheckoutUsage>,
    val serviceUsages: List<CheckoutUsage>,val productUsages: List<CheckoutUsage>,val passengerTotal: BigDecimal)
data class CheckoutUsage(val usageId: Long,val quantity: Int?,val unitPrice: BigDecimal,
    val discountAmount: BigDecimal,val finalAmount: BigDecimal,val adjustmentAmount: BigDecimal?)
data class CheckoutSnapshot(val billId: Long,val billCode: String,val bookingId: Long,val totalAmount: BigDecimal)
data class ServiceBillLine(val id: Long,val bookingPassengerId: Long,val passengerName: String?,
    val usageType: String,val usageId: Long,val quantity: Int,val unitPrice: BigDecimal,
    val discountAmount: BigDecimal,val finalAmount: BigDecimal)
data class ServiceBillPayment(val paymentId: Long,val status: String,val paymentUrl: String?,val expiresAt: String?)
data class ServiceBill(val bookingId: Long,val bookingCode: String,val billId: Long?,val billCode: String?,
    val billStatus: String?,val accountStatus: String,val currency: String,val totalAmount: BigDecimal?,
    val createdAt: String?,val items: List<ServiceBillLine>,val payments: List<ServiceBillPayment>,
    val paymentAvailable: Boolean,val paymentState: String)
