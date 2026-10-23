package com.project.cruise.android.data.dto.chat

data class ChatMessageRequest(
    val message: String,
    val sessionId: String? = null
)