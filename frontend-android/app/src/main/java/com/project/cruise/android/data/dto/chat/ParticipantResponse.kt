package com.project.cruise.android.data.dto.chat

data class ParticipantResponse(
    val id: String,
    val userId: String,
    val participantRole: String,
    val joinedAt: String?,
    val lastReadAt: String?
)