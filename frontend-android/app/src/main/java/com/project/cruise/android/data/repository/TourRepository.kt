package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.data.network.api.TourApiService

class TourRepository(
    private val apiService: TourApiService // Đổi sang TourApiService
) {
    suspend fun getPublicTours(): List<PublicTourSummaryResponse> {
        return apiService.getPublicTours()
    }

    suspend fun getPublicTourDetail(tourId: String): PublicTourDetailResponse {
        return apiService.getPublicTourDetail(tourId)
    }
}