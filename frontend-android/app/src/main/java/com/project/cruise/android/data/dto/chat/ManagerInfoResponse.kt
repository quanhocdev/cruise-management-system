package com.project.cruise.android.data.dto.chat

data class ManagerInfoResponse(
    val userId: String,
    val username: String?,
    val email: String?,
    val fullName: String?,
    val phone: String?,
    val avatarPublicId: String?
)