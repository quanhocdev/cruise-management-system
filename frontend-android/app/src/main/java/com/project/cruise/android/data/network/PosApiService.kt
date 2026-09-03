package com.project.cruise.android.data.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PosApiService {
    @POST("api/v1/pos/identify")
    suspend fun identify(
        @Header("X-Terminal-Code") terminalCode: String,
        @Header("X-POS-Key") posKey: String,
        @Body request: PosIdentityRequest
    ): PosIdentityResponse
    @POST("api/v1/pos/transactions/sync")
    suspend fun sync(
        @Header("X-Terminal-Code") terminalCode: String,
        @Header("X-POS-Key") posKey: String,
        @Body request: PosSyncRequest
    ): PosSyncResponse
}

data class PosIdentityRequest(val scanType: String, val scannedValue: String)
data class PosIdentityResponse(
    val status: String, val reason: String?, val passengerVoyageId: Long?,
    val fullName: String?, val bookingCode: String?, val voyageId: String?,
    val cabinId: String?, val embarkationStatus: String?
)

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
