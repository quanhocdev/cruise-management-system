package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageRequest
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConvenienceApiService {
    @POST("api/convenience/nfc/resolve")
    suspend fun resolveConvenienceNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/convenience/product-tours/tour/{tourId}")
    suspend fun getConvenienceProductTours(
        @Path("tourId") tourId: String
    ): List<ProductTourResponse>

    @GET("api/convenience/service-tours/tour/{tourId}")
    suspend fun getConvenienceServiceTours(
        @Path("tourId") tourId: String
    ): List<ServiceTourResponse>

    @POST("api/convenience/product-usages")
    suspend fun createProductUsage(
        @Body request: ProductUsageRequest
    ): ProductUsageResponse

    @POST("api/convenience/service-usages")
    suspend fun createServiceUsage(
        @Body request: ServiceUsageRequest
    ): ServiceUsageResponse
}