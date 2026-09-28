package com.project.cruise.android.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PosApiService {
    @POST("api/finance/scan")
    suspend fun scanQrCode(
        @Body request: QrScanRequest
    ): ScanResponse

    @GET("api/convenience/products")
    suspend fun getConvenienceProducts(): List<ConvenienceProductResponse>

    @GET("api/convenience/services")
    suspend fun getConvenienceServices(): List<ConvenienceServiceResponse>
}

// DTO khớp với bên Backend
data class QrScanRequest(val bookingCode: String)

data class ScanResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Long?
)

data class ConvenienceProductResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val stockQuantity: Int?,
    val imageUrl: String?,
    val status: String?
)

data class ConvenienceServiceResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val durationMinutes: Int?,
    val maxPassengers: Int?,
    val imageUrl: String?
)
