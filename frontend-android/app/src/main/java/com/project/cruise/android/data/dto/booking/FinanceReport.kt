package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class FinanceReport(val tourId:String,val currency:String,val generatedAt:String,
    val charges:ReportCharges,val settlement:ReportSettlement,val bills:ReportBillPage)
data class ReportCharges(val bookings:Long,val gross:BigDecimal,val voided:BigDecimal,val refunded:BigDecimal,val net:BigDecimal)
data class ReportSettlement(val billCount:Long,val billed:BigDecimal,val unsettledBillCount:Long,val paymentAvailable:Boolean,
    val collected:BigDecimal?,val returned:BigDecimal?,val refundPending:BigDecimal?,val netCollected:BigDecimal?,
    val unpaidBillAmount:BigDecimal?,val unverifiedPaidBills:Long,val reviewCount:Long)
data class ReportBillPage(val page:Int,val size:Int,val totalItems:Long,val items:List<ReportBill>)
data class ReportBill(val billId:Long,val bookingId:Long,val bookingCode:String,val billCode:String,val status:String,
    val amount:BigDecimal,val createdAt:String,val collected:BigDecimal?,val returned:BigDecimal?,val needsReview:Boolean)
