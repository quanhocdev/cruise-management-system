package com.project.cruise.android.data.remote

import com.project.cruise.android.data.dto.chat.DialogflowDetectIntentRequest
import com.project.cruise.android.data.dto.chat.DialogflowDetectIntentResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DialogflowApiService {
    @POST("v2/projects/{projectId}/agent/sessions/{sessionId}:detectIntent")
    suspend fun detectIntent(
        @Path("projectId") projectId: String,
        @Path("sessionId") sessionId: String,
        @Header("Authorization") bearerToken: String,
        @Body request: DialogflowDetectIntentRequest
    ): DialogflowDetectIntentResponse
}