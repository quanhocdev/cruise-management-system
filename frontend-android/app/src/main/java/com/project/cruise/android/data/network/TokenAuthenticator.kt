package com.project.cruise.android.data.network

import android.util.Log
import com.project.cruise.android.data.auth.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApi: ApiService
) : Authenticator {

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        Log.d(
            "AUTH_DEBUG",
            "401 RECEIVED - Access token expired or unauthorized"
        )

        // Tránh refresh vô hạn
        if (response.request().header("X-Auth-Retry") == "true") {

            Log.e(
                "AUTH_DEBUG",
                "RETRY FAILED - CLEAR TOKENS"
            )

            tokenManager.clearTokens()

            return null
        }

        val refreshToken =
            tokenManager.getRefreshToken()

        if (refreshToken.isNullOrBlank()) {

            Log.e(
                "AUTH_DEBUG",
                "NO REFRESH TOKEN"
            )

            return null
        }

        Log.d(
            "AUTH_DEBUG",
            "REFRESH TOKEN FOUND - CALLING /refresh"
        )

        return try {

            val refreshCall =
                refreshApi.refresh(
                    "Bearer $refreshToken"
                )

            val refreshResponse =
                refreshCall.execute()

            if (!refreshResponse.isSuccessful) {

                Log.e(
                    "AUTH_DEBUG",
                    "REFRESH FAILED - HTTP ${refreshResponse.code()}"
                )

                tokenManager.clearTokens()

                return null
            }

            val body =
                refreshResponse.body()

            if (body == null) {

                Log.e(
                    "AUTH_DEBUG",
                    "REFRESH FAILED - EMPTY BODY"
                )

                tokenManager.clearTokens()

                return null
            }

            Log.d(
                "AUTH_DEBUG",
                "REFRESH SUCCESS"
            )

            Log.d(
                "AUTH_DEBUG",
                "NEW ACCESS TOKEN RECEIVED"
            )

            tokenManager.saveAccessToken(
                body.accessToken
            )

            Log.d(
                "AUTH_DEBUG",
                "NEW ACCESS TOKEN SAVED"
            )

            // Gửi lại request ban đầu
            response.request()
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${body.accessToken}"
                )
                .header(
                    "X-Auth-Retry",
                    "true"
                )
                .build()

        } catch (e: Exception) {

            Log.e(
                "AUTH_DEBUG",
                "REFRESH ERROR: ${e.message}",
                e
            )

            tokenManager.clearTokens()

            null
        }
    }
}