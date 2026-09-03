package com.project.cruise.android.data.network

import com.project.cruise.android.data.auth.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(
                "${AppConfig.HTTP_BASE_URL}/"
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val refreshApiService: ApiService =
        retrofit.create(ApiService::class.java)

    val posApiService: PosApiService =
        retrofit.create(PosApiService::class.java)

    fun createApiService(
        tokenManager: TokenManager,
        retryOnConnectionFailure: Boolean = true
    ): ApiService {

        val authenticator =
            TokenAuthenticator(
                tokenManager = tokenManager,
                refreshApi = refreshApiService
            )

        val client =
            OkHttpClient.Builder()
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .addInterceptor(
                    AuthInterceptor(tokenManager)
                )
                .authenticator(authenticator)
                .build()

        return Retrofit.Builder()
            .baseUrl(
                "${AppConfig.HTTP_BASE_URL}/"
            )
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}
