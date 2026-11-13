package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.passenger.feedback.FeedbackInvitationResponse
import com.project.cruise.android.data.dto.passenger.feedback.FeedbackResponse
import com.project.cruise.android.data.dto.passenger.feedback.SubmitFeedbackRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface FeedbackApiService {

    @GET("api/passengers/feedback/invitations")
    suspend fun getMyInvitations(): List<FeedbackInvitationResponse>

    @GET("api/passengers/feedback/invitations/pending")
    suspend fun getMyPendingInvitations(): List<FeedbackInvitationResponse>

    @POST("api/passengers/feedback/invitations/{id}/submit")
    suspend fun submitFeedback(
        @Path("id") id: UUID,
        @Body request: SubmitFeedbackRequest
    ): FeedbackResponse

    @POST("api/passengers/feedback/invitations/{id}/skip")
    suspend fun skipInvitation(
        @Path("id") id: UUID
    ): FeedbackInvitationResponse

    @GET("api/passengers/feedback/invitations/{id}/feedback")
    suspend fun getMyFeedback(
        @Path("id") id: UUID
    ): FeedbackResponse
}