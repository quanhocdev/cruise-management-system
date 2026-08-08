package com.project.cruise.android.data.dto.auth

data class JwtResponse(
    val token: String,
    val refreshToken: String,
    val username: String,
    val role: String
)