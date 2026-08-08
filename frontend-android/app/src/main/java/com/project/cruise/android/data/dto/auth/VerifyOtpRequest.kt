package com.project.cruise.android.data.dto.auth

data class VerifyOtpRequest(
    val userId: Long,
    val otp: String
)