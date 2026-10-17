package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class SpendingSummary(
    val bookingId: Long,
    val currency: String,
    val accountStatus: String,
    val creditLimit: BigDecimal?,
    val totalCharges: BigDecimal,
    val capturedLedgerTotal: BigDecimal,
    val ledgerComplete: Boolean,
    val totalItems: Long,
    val page: Int,
    val size: Int,
    val items: List<SpendingLine>
)

data class SpendingLine(
    val type: String,
    val usageId: Long,
    val bookingPassengerId: Long,
    val targetId: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val discountAmount: BigDecimal,
    val finalAmount: BigDecimal,
    val usedAt: String
)
