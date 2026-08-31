package com.project.cruise.android.data.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PosApiService {
    @POST("api/v1/pos/transactions/sync")
    suspend fun sync(
        @Header("X-Terminal-Code") terminalCode: String,
        @Header("X-POS-Key") posKey: String,
        @Body request: PosSyncRequest
    ): PosSyncResponse
}

data class PosSyncRequest(
    val localId: String,
    val scanType: String,
    val scannedValue: String,
    val createdAt: String
)

data class PosSyncResponse(
    val serverId: Long,
    val localId: String,
    val status: String,
    val receivedAt: String,
    val duplicate: Boolean
)
