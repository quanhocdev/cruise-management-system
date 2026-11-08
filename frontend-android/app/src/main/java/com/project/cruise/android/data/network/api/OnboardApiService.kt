package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageRequest
import com.project.cruise.android.data.dto.onboard.ActivityCruiseUsageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OnboardApiService {
    @POST("api/onboard/nfc/resolve")
    suspend fun resolveOnboardNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/onboard/activity-cruise-tours/tour/{tourId}")
    suspend fun getOnboardActivityCruiseTours(
        @Path("tourId") tourId: String
    ): List<ActivityCruiseTourResponse>

    @POST("api/onboard/activity-cruise-usages")
    suspend fun createActivityCruiseUsage(
        @Body request: ActivityCruiseUsageRequest
    ): ActivityCruiseUsageResponse
}