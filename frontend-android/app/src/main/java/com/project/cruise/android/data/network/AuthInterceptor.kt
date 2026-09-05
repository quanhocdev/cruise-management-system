package com.project.cruise.android.data.network

import com.project.cruise.android.data.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token = tokenManager.getAccessToken()

        if (!token.isNullOrBlank()) {
            val request = chain.request()
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer $token"
                )
                .build()

            return chain.proceed(request)
        }

        return chain.proceed(
            chain.request()
        )
    }
}
