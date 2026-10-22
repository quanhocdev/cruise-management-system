package com.project.cruise.android.data.dto.chat

data class ChatMessageResponse(
    val message: String,
    val sessionId: String,
    val intentName: String?
)