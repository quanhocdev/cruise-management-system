package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.data.network.ApiService

class TourRepository(
    private val apiService: ApiService
) {
    suspend fun getPublicTours(): List<PublicTourSummaryResponse> {
        return apiService.getPublicTours()
    }

    suspend fun getPublicTourDetail(tourId: String): PublicTourDetailResponse {
        return apiService.getPublicTourDetail(tourId)
    }
}