package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ProductUsageRequest
import com.project.cruise.android.data.dto.convenience.ProductUsageResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceUsageRequest
import com.project.cruise.android.data.dto.convenience.ServiceUsageResponse
import com.project.cruise.android.data.network.RetrofitClient

class ConvenienceRepository(context: Context) {

    private val appContext = context.applicationContext

    private val apiService =
        RetrofitClient.createApiService(
            TokenManager(appContext)
        )

    suspend fun resolveNfc(
        nfcCardUid: String
    ): NfcResolveResponse {
        return apiService.resolveConvenienceNfc(
            NfcResolveRequest(
                nfcCardUid = nfcCardUid
            )
        )
    }

    suspend fun getProductTours(
        tourId: String
    ): List<ProductTourResponse> {
        return apiService.getConvenienceProductTours(tourId)
    }

    suspend fun getServiceTours(
        tourId: String
    ): List<ServiceTourResponse> {
        return apiService.getConvenienceServiceTours(tourId)
    }
    suspend fun createProductUsage(
        nfcCardUid: String,
        productTourId: String,
        quantity: Int
    ): ProductUsageResponse {
        return apiService.createProductUsage(
            ProductUsageRequest(
                nfcCardUid = nfcCardUid,
                productTourId = productTourId,
                quantity = quantity
            )
        )
    }

    suspend fun createServiceUsage(
        nfcCardUid: String,
        serviceTourId: String
    ): ServiceUsageResponse {
        return apiService.createServiceUsage(
            ServiceUsageRequest(
                nfcCardUid = nfcCardUid,
                serviceTourId = serviceTourId
            )
        )
    }

}