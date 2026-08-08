package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import com.project.cruise.android.data.network.ApiService

class AuthRepository(
    private val apiService: ApiService
) {

    suspend fun login(
        username: String,
        password: String
    ): JwtResponse {

        val request = LoginRequest(
            username = username,
            password = password
        )

        return apiService.login(request)
    }

    suspend fun register(
        username: String,
        password: String,
        email: String?
    ): RegisterResponse {

        val request = RegisterRequest(
            username = username,
            password = password,
            email = email
        )

        return apiService.register(request)
    }

    suspend fun verifyEmail(
        userId: Long,
        otp: String
    ): Map<String, String> {

        val request = VerifyOtpRequest(
            userId = userId,
            otp = otp
        )

        return apiService.verifyEmail(request)
    }
}