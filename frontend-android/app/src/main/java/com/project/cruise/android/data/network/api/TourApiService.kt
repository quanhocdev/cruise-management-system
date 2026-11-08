package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TourApiService {
    @GET("api/public/tours")
    suspend fun getPublicTours(): List<PublicTourSummaryResponse>

    @GET("api/public/tours/{id}")
    suspend fun getPublicTourDetail(
        @Path("id") id: String
    ): PublicTourDetailResponse
}