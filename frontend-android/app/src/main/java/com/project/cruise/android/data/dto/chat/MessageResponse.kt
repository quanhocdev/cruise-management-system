package com.project.cruise.android.data.dto.chat

data class MessageResponse(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val content: String,
    val messageType: String,
    val messageStatus: String,
    val sentAt: String?,
    val readAt: String?
)