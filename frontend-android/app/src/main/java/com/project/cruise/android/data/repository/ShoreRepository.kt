package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageRequest
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageResponse
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.network.RetrofitClient

class ShoreRepository(context: Context) {

    private val appContext = context.applicationContext

    private val apiService: ApiService =
        RetrofitClient.createApiService(TokenManager(appContext))

    suspend fun resolveNfc(
        nfcCardUid: String
    ): NfcResolveResponse {
        return apiService.resolveShoreNfc(
            NfcResolveRequest(
                nfcCardUid = nfcCardUid
            )
        )
    }

    suspend fun getActivityVisitTours(
        tourId: String
    ): List<ActivityVisitTourResponse> {
        return apiService.getShoreActivityVisitTours(tourId)
    }

    suspend fun createActivityVisitUsage(
        nfcCardUid: String,
        visitTourId: String
    ): ActivityVisitUsageResponse {
        return apiService.createActivityVisitUsage(
            ActivityVisitUsageRequest(
                nfcCardUid = nfcCardUid,
                visitTourId = visitTourId
            )
        )
    }
}