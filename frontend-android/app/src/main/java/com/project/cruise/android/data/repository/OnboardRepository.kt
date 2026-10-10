package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageRequest
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageResponse
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.auth.TokenManager
import java.util.UUID

class OnboardRepository(context: Context) {

    private val appContext = context.applicationContext

    private val apiService: ApiService =
        RetrofitClient.createApiService(TokenManager(appContext))

    suspend fun resolveNfc(
        nfcCardUid: String
    ): NfcResolveResponse {
        return apiService.resolveOnboardNfc(
            NfcResolveRequest(
                nfcCardUid = nfcCardUid
            )
        )
    }

    suspend fun getActivityCruiseTours(
        tourId: String
    ): List<ActivityCruiseTourResponse> {
        return apiService.getOnboardActivityCruiseTours(tourId)
    }

    suspend fun createActivityCruiseUsage(
        nfcCardUid: String,
        activityCruiseTourId: String
    ): ActivityCruiseUsageResponse {
        return apiService.createActivityCruiseUsage(
            ActivityCruiseUsageRequest(
                nfcCardUid = nfcCardUid,
                activityCruiseTourId = activityCruiseTourId
            )
        )
    }
}