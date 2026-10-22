
package com.project.cruise.android.data.dto.auth

data class GoogleLoginResponse(
    val status: String? = null,
    val userId: Long? = null,
    val message: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val username: String? = null,
    val role: String? = null
) {
    val isOtpRequired: Boolean
        get() = status == "OTP_REQUIRED"

    fun toJwtResponse(): JwtResponse? {
        val accessToken = token ?: return null
        val refresh = refreshToken ?: return null
        val user = username ?: return null
        val userRole = role ?: return null

        return JwtResponse(
            token = accessToken,
            refreshToken = refresh,
            username = user,
            role = userRole
        )
    }
}
