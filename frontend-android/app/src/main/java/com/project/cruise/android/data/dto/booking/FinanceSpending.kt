package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class FinanceSpendingAccount(val bookingId: Long, val bookingCode: String, val contactName: String,
    val status: String, val creditLimit: BigDecimal?, val spent: BigDecimal, val pendingHistoryCount: Long, val canEdit: Boolean)
data class SpendingLimitRequest(val creditLimit: BigDecimal, val reason: String, val clientRequestId: String? = null)
data class SpendingLimitResult(val bookingId: Long, val creditLimit: BigDecimal, val spent: BigDecimal, val remaining: BigDecimal)
data class LedgerHistoryRequest(val clientRequestId: String)
data class LedgerHistoryResult(val bookingId: Long, val importedCount: Int, val balance: BigDecimal)
