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

        println("AUTH_DEBUG: API ${chain.request().url()}")

        if (!token.isNullOrBlank()) {
            println("AUTH_DEBUG: ACCESS TOKEN FOUND")
            println("AUTH_DEBUG: ACCESS TOKEN = $token")

            val request = chain.request()
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer $token"
                )
                .build()

            return chain.proceed(request)
        }

        println("AUTH_DEBUG: NO ACCESS TOKEN")

        return chain.proceed(
            chain.request()
        )
    }
}