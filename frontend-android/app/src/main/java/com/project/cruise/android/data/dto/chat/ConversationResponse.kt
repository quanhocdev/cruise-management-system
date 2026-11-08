package com.project.cruise.android.data.dto.chat

data class ConversationResponse(
    val id: String,
    val bookingId: Long,
    val bookingCode: String?,
    val department: String,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)