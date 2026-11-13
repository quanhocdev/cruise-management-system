package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.chat.ChatPageResponse
import com.project.cruise.android.data.dto.chat.ConfirmedBookingResponse
import com.project.cruise.android.data.dto.chat.ConversationResponse
import com.project.cruise.android.data.dto.chat.CreateConversationRequest
import com.project.cruise.android.data.dto.chat.ManagerInfoResponse
import com.project.cruise.android.data.dto.chat.MessageResponse
import com.project.cruise.android.data.dto.chat.ParticipantResponse
import com.project.cruise.android.data.dto.chat.SendMessageRequest
import com.project.cruise.android.data.dto.chatbox.ChatMessageRequest
import com.project.cruise.android.data.dto.chatbox.ChatMessageResponse
import com.project.cruise.android.data.dto.chatbox.ChatSessionRequest
import com.project.cruise.android.data.dto.chatbox.ChatWelcomeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @POST("api/chat/message")
    suspend fun sendChatMessage(
        @Body request: ChatMessageRequest
    ): ChatMessageResponse

    @POST("api/chat/welcome")
    suspend fun openChat(
        @Body request: ChatSessionRequest
    ): ChatWelcomeResponse

    @GET("api/message/bookings")
    suspend fun getChatBookings(): List<ConfirmedBookingResponse>

    @GET("api/message/managers/{roleName}")
    suspend fun getChatManagers(
        @Path("roleName") roleName: String
    ): List<ManagerInfoResponse>

    @POST("api/message/conversations")
    suspend fun createOrGetConversation(
        @Body request: CreateConversationRequest
    ): ConversationResponse

    @GET("api/message/conversations")
    suspend fun getMyConversations(): List<ConversationResponse>

    @GET("api/message/conversations/{conversationId}")
    suspend fun getConversationDetail(
        @Path("conversationId") conversationId: String
    ): ConversationResponse

    @GET("api/message/conversations/{conversationId}/participants")
    suspend fun getConversationParticipants(
        @Path("conversationId") conversationId: String
    ): List<ParticipantResponse>

    @GET("api/message/conversations/{conversationId}/messages")
    suspend fun getChatMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ChatPageResponse<MessageResponse>

    @POST("api/message/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body request: SendMessageRequest
    ): MessageResponse
}