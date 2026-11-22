package com.project.cruise.android.data.network.api

import com.google.gson.JsonElement
import com.project.cruise.android.data.dto.passenger.feedback.SubmitFeedbackRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FeedbackApiService {

    @GET("api/passengers/feedback/invitations")
    suspend fun getMyInvitations(): JsonElement

    @GET("api/passengers/feedback/invitations/pending")
    suspend fun getMyPendingInvitations(): JsonElement

    @POST("api/passengers/feedback/invitations/{id}/submit")
    suspend fun submitFeedback(
        @Path("id") id: String,
        @Body request: SubmitFeedbackRequest
    ): JsonElement

    @POST("api/passengers/feedback/invitations/{id}/skip")
    suspend fun skipInvitation(
        @Path("id") id: String
    ): JsonElement

    @GET("api/passengers/feedback/invitations/{id}/feedback")
    suspend fun getMyFeedback(
        @Path("id") id: String
    ): JsonElement
}