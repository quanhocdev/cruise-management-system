
package com.project.cruise.android.data.repository

import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.auth.GoogleLoginRequest
import com.project.cruise.android.data.dto.auth.GoogleLoginResponse
import com.project.cruise.android.data.dto.auth.GoogleVerifyOtpRequest
import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.LoginRequest
import com.project.cruise.android.data.dto.auth.RegisterRequest
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.dto.auth.UserInfoResponse
import com.project.cruise.android.data.dto.auth.VerifyOtpRequest
import com.project.cruise.android.data.network.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    // LOGIN BẰNG TÀI KHOẢN VÀ MẬT KHẨU
    suspend fun login(
        username: String,
        password: String
    ): JwtResponse {
        val response = apiService.login(
            LoginRequest(
                username = username,
                password = password
            )
        )

        tokenManager.saveTokens(
            accessToken = response.token,
            refreshToken = response.refreshToken
        )

        return response
    }

    // GOOGLE LOGIN
    // Nếu backend yêu cầu OTP, chưa lưu JWT.
    suspend fun googleLogin(
        firebaseIdToken: String
    ): GoogleLoginResponse {
        require(firebaseIdToken.isNotBlank()) {
            "Firebase ID token không được để trống"
        }

        val response = apiService.googleLogin(
            GoogleLoginRequest(idToken = firebaseIdToken)
        )

        if (!response.isOtpRequired) {
            val jwt = response.toJwtResponse()
                ?: throw IllegalStateException(
                    response.message
                        ?: "Backend không trả về JWT hợp lệ"
                )

            // Chỉ cho phép hành khách đăng nhập vào ứng dụng này.
            if (!jwt.role.isPassengerRole()) {
                throw IllegalStateException(
                    "Ứng dụng này chỉ dành cho hành khách"
                )
            }

            tokenManager.saveTokens(
                accessToken = jwt.token,
                refreshToken = jwt.refreshToken
            )
        }

        return response
    }

    // XÁC THỰC OTP CỦA GOOGLE LOGIN
    suspend fun verifyGoogleOtp(
        userId: String,
        otp: String
    ): JwtResponse {
        val response = apiService.verifyGoogleOtp(
            GoogleVerifyOtpRequest(
                userId = userId,
                otp = otp
            )
        )

        if (!response.role.isPassengerRole()) {
            tokenManager.clearTokens()
            throw IllegalStateException(
                "Ứng dụng này chỉ dành cho hành khách"
            )
        }

        tokenManager.saveTokens(
            accessToken = response.token,
            refreshToken = response.refreshToken
        )

        return response
    }

    // REGISTER
    suspend fun register(
        username: String,
        password: String,
        email: String?
    ): RegisterResponse {
        return apiService.register(
            RegisterRequest(
                username = username,
                password = password,
                email = email
            )
        )
    }

    // OTP ĐĂNG KÝ THÔNG THƯỜNG — GIỮ NGUYÊN ENDPOINT CŨ
    suspend fun verifyEmail(
        userId: String,
        otp: String
    ): Map<String, String> {
        return apiService.verifyEmail(
            VerifyOtpRequest(
                userId = userId,
                otp = otp
            )
        )
    }

    // LẤY THÔNG TIN TÀI KHOẢN
    suspend fun getCurrentUser(): UserInfoResponse {
        return apiService.getCurrentUser()
    }

    fun hasStoredSession(): Boolean =
        !tokenManager.getAccessToken().isNullOrBlank() ||
                !tokenManager.getRefreshToken().isNullOrBlank()

    fun logout() {
        tokenManager.clearTokens()
    }

    private fun String.isPassengerRole(): Boolean =
        trim().uppercase().removePrefix("ROLE_") == "PASSENGER"
}
