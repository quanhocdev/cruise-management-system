package com.project.cruise.android.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(
                "${AppConfig.HTTP_BASE_URL}/api/"
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val apiService: ApiService =
        retrofit.create(ApiService::class.java)
}