package com.project.cruise.android.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// DTO chứa tọa độ gửi lên
data class VesselLocationRequest(
    val latitude: Double,
    val longitude: Double
)

interface VesselTrackingApi {
    @POST("api/vessel-tracking/tours/{tourId}/location")
    suspend fun updateVesselLocation(
        @Header("Authorization") token: String,
        @Path("tourId") tourId: Long,
        @Body location: VesselLocationRequest
    ): Response<Unit>
}