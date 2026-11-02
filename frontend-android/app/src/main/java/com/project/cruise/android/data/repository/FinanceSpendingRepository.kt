package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient
import java.math.BigDecimal

interface FinanceSpendingSource {
    suspend fun find(code: String): FinanceSpendingAccount
    suspend fun setLimit(id: Long, amount: BigDecimal, reason: String): SpendingLimitResult
    suspend fun importHistory(id: Long): LedgerHistoryResult
}

class FinanceSpendingRepository(context: Context) : FinanceSpendingSource {
    private val api = RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands = OnlinePosCommands(context.applicationContext)
    override suspend fun find(code: String) = api.getFinanceSpending(code)
    override suspend fun setLimit(id: Long, amount: BigDecimal, reason: String): SpendingLimitResult {
        val request = SpendingLimitRequest(amount, reason)
        return commands.execute("SPENDING_LIMIT:$id", request) { key -> api.setSpendingLimit(id, request.copy(clientRequestId=key)) }
    }
    override suspend fun importHistory(id: Long): LedgerHistoryResult =
        commands.execute("LEDGER_HISTORY:$id", mapOf("bookingId" to id)) { key -> api.importLedgerHistory(id, LedgerHistoryRequest(key)) }
}
