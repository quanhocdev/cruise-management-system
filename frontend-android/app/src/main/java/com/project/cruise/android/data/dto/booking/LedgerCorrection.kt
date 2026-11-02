package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class LedgerEntry(
    val id: Long, val bookingPassengerId: Long, val sourceType: String, val sourceUsageId: Long,
    val amount: BigDecimal, val adjustedAmount: BigDecimal, val remainingAmount: BigDecimal,
    val benefitQuantity: Int?, val restoredQuota: Int, val createdAt: String,
    val canVoid: Boolean, val canRefund: Boolean, val blockedReason: String?
)
data class LedgerPage(val bookingId: Long, val bookingCode: String, val status: String,
    val totalItems: Long, val page: Int, val size: Int, val items: List<LedgerEntry>)
data class LedgerCorrectionRequest(val type: String, val amount: BigDecimal?, val reason: String, val clientRequestId: String? = null)
data class LedgerCorrectionResult(val entryId: Long, val refEntryId: Long, val type: String,
    val amount: BigDecimal, val remainingAmount: BigDecimal, val restoredQuota: Int)
