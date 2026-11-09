package com.project.cruise.android.data.dto.chatbox

data class ChatMessageRequest(
    val message: String,
    val sessionId: String? = null
)