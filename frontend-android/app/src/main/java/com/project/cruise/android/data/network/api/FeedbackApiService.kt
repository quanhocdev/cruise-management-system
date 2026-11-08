package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.feedback.FeedbackDraft
import com.project.cruise.android.data.dto.feedback.FeedbackOptions
import com.project.cruise.android.data.dto.feedback.MyFeedback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FeedbackApiService {
    @GET("api/v1/feedbacks/me")
    suspend fun getMyFeedback(): List<MyFeedback>

    @GET("api/v1/feedbacks/bookings/{id}/options")
    suspend fun getFeedbackOptions(
        @Path("id") id: Long
    ): FeedbackOptions

    @POST("api/v1/feedbacks")
    suspend fun createFeedback(
        @Body request: FeedbackDraft
    ): MyFeedback
}