package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.auth.GoogleLoginRequest
import com.project.cruise.android.data.dto.auth.GoogleLoginResponse
import com.project.cruise.android.data.dto.auth.GoogleVerifyOtpRequest
import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RefreshResponse
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.UserInfoResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): JwtResponse

    @POST("api/auth/google/login")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): GoogleLoginResponse

    @POST("api/auth/google/verify-otp")
    suspend fun verifyGoogleOtp(
        @Body request: GoogleVerifyOtpRequest
    ): JwtResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyOtpRequest
    ): Map<String, String>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): UserInfoResponse

    @POST("api/auth/refresh")
    fun refresh(
        @Header("Authorization") authorization: String
    ): Call<RefreshResponse>
}