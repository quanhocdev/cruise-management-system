package com.project.cruise.android.data.network

import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): JwtResponse

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyOtpRequest
    ): Map<String, String>
}