package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.nfc.NfcResolveRequest
import com.project.cruise.android.data.dto.nfc.NfcResolveResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageRequest
import com.project.cruise.android.data.dto.shore.ActivityVisitUsageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShoreApiService {
    @POST("api/shore/nfc/resolve")
    suspend fun resolveShoreNfc(
        @Body request: NfcResolveRequest
    ): NfcResolveResponse

    @GET("api/shore/visit-tours/tour/{tourId}")
    suspend fun getShoreActivityVisitTours(
        @Path("tourId") tourId: String
    ): List<ActivityVisitTourResponse>

    @POST("api/shore/activity-visit-usages")
    suspend fun createActivityVisitUsage(
        @Body request: ActivityVisitUsageRequest
    ): ActivityVisitUsageResponse
}