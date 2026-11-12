package com.project.cruise.android.data.repository

import com.project.cruise.android.data.network.VesselLocationRequest
import com.project.cruise.android.data.network.VesselTrackingApi
import retrofit2.Response

class VesselRepository(private val vesselTrackingApi: VesselTrackingApi) {

    suspend fun sendLocation(token: String, tourId: Long, lat: Double, lng: Double): Boolean {
        return try {
            val request = VesselLocationRequest(latitude = lat, longitude = lng)
            val response = vesselTrackingApi.updateVesselLocation(
                token = "Bearer $token",
                tourId = tourId,
                location = request
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}