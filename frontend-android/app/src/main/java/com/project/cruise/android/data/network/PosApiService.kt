package com.project.cruise.android.data.network

import retrofit2.http.Body
import retrofit2.http.POST

interface PosApiService {
    @POST("api/finance/scan")
    suspend fun scanQrCode(
        @Body request: QrScanRequest
    ): ScanResponse
}

// DTO khớp với bên Backend
data class QrScanRequest(val bookingCode: String)

data class ScanResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Long?
)