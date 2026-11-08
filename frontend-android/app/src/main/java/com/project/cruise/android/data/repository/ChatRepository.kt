package com.project.cruise.android.data.repository

import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.chat.*
import com.project.cruise.android.data.network.ChatSocketService
import com.project.cruise.android.data.network.api.ChatApiService

class ChatRepository(
    private val apiService: ChatApiService, // Đổi sang ChatApiService
    private val tokenManager: TokenManager,
    private val chatSocketService: ChatSocketService,
    private val baseUrl: String
) {

    // ==================== REST API ====================

    suspend fun getChatBookings(): List<ConfirmedBookingResponse> {
        return apiService.getChatBookings()
    }

    suspend fun getChatManagers(
        roleName: String
    ): List<ManagerInfoResponse> {
        return apiService.getChatManagers(roleName)
    }

    suspend fun createOrGetConversation(
        bookingId: Long,
        department: String
    ): ConversationResponse {
        return apiService.createOrGetConversation(
            CreateConversationRequest(
                bookingId = bookingId,
                department = department
            )
        )
    }

    suspend fun getMyConversations(): List<ConversationResponse> {
        return apiService.getMyConversations()
    }

    suspend fun getConversationDetail(
        conversationId: String
    ): ConversationResponse {
        return apiService.getConversationDetail(conversationId)
    }

    suspend fun getParticipants(
        conversationId: String
    ): List<ParticipantResponse> {
        return apiService.getConversationParticipants(conversationId)
    }

    suspend fun getMessages(
        conversationId: String,
        page: Int = 0,
        size: Int = 20
    ): ChatPageResponse<MessageResponse> {
        return apiService.getChatMessages(
            conversationId = conversationId,
            page = page,
            size = size
        )
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String
    ): MessageResponse {
        require(content.isNotBlank()) {
            "Nội dung tin nhắn không được để trống"
        }

        require(content.length <= 5000) {
            "Tin nhắn không được vượt quá 5000 ký tự"
        }

        return apiService.sendMessage(
            conversationId = conversationId,
            request = SendMessageRequest(content.trim())
        )
    }

    // ==================== WEBSOCKET ====================

    fun connectToConversation(
        conversationId: String,
        onConnected: () -> Unit,
        onMessage: (MessageResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val accessToken = tokenManager.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            onError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.")
            return
        }

        chatSocketService.connect(
            baseUrl = baseUrl,
            accessToken = accessToken,
            conversationId = conversationId,
            onConnected = onConnected,
            onMessage = onMessage,
            onError = onError
        )
    }

    fun disconnectChat() {
        chatSocketService.disconnect()
    }

    fun isSocketConnected(): Boolean {
        return chatSocketService.isConnected()
    }

    fun getSubscribedConversationId(): String? {
        return chatSocketService.getSubscribedConversationId()
    }
}