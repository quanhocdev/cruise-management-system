package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient

interface LedgerCorrectionSource {
    suspend fun list(code: String, page: Int): LedgerPage
    suspend fun correct(id: Long, request: LedgerCorrectionRequest): LedgerCorrectionResult
}
class LedgerCorrectionRepository(context: Context, private val finance: Boolean): LedgerCorrectionSource {
    private val api = RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands = OnlinePosCommands(context.applicationContext)
    override suspend fun list(code: String, page: Int) = if (finance) api.getFinanceLedger(code,page) else api.getConvenienceLedger(code,page)
    override suspend fun correct(id: Long, request: LedgerCorrectionRequest) = commands.execute("LEDGER_CORRECTION:$id",request) { key ->
        if (finance) api.correctFinanceLedger(id,request.copy(clientRequestId=key))
        else api.correctConvenienceLedger(id,request.copy(clientRequestId=key))
    }
}
