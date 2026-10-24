package com.project.cruise.android.data.dto.auth

data class VerifyOtpRequest(
    val userId: String,
    val otp: String
)