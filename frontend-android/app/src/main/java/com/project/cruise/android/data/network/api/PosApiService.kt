package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.pos.ConvenienceProductResponse
import com.project.cruise.android.data.dto.pos.ConvenienceServiceResponse
import com.project.cruise.android.data.dto.pos.QrScanRequest
import com.project.cruise.android.data.dto.pos.ScanResponse
import com.project.cruise.android.data.dto.pos.ShoreVisitTourResponse
import com.project.cruise.android.data.network.ScanResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private val pos: Any

interface PosApiService {
    @POST("api/finance/scan")
    suspend fun scanQrCode(
        @Body request: QrScanRequest
    ): ScanResponse

    @GET("api/convenience/products")
    suspend fun getConvenienceProducts(): List<ConvenienceProductResponse>

    @GET("api/convenience/services")
    suspend fun getConvenienceServices(): List<ConvenienceServiceResponse>

    @GET("api/shore/visit-tours")
    suspend fun getShoreVisitTours(): List<ShoreVisitTourResponse>
}