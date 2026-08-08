package com.project.cruise.android.data.dto.auth

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String?,
    val fullName: String?
)