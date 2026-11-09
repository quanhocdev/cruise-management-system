package com.project.cruise.android.data.network.core

import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.TokenAuthenticator
import com.project.cruise.android.data.network.api.AuthApiService
import com.project.cruise.android.data.network.api.BookingApiService
import com.project.cruise.android.data.network.api.ChatApiService
import com.project.cruise.android.data.network.api.ConvenienceApiService
import com.project.cruise.android.data.network.api.FeedbackApiService
import com.project.cruise.android.data.network.api.FinanceApiService
import com.project.cruise.android.data.network.api.NotificationApiService
import com.project.cruise.android.data.network.api.OnboardApiService
import com.project.cruise.android.data.network.api.PosApiService
import com.project.cruise.android.data.network.api.ShoreApiService
import com.project.cruise.android.data.network.api.StaffApiService
import com.project.cruise.android.data.network.api.TourApiService
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

    // 1. Đổi kiểu dữ liệu từ ApiService cũ sang AuthApiService vì hàm refresh() nằm ở đây
    val refreshApiService: AuthApiService =
        retrofit.create(AuthApiService::class.java)

    // 2. Hàm chung để tạo bất kỳ Api Service nào có kèm Interceptor & Authenticator
    private fun <T> createService(
        tokenManager: TokenManager,
        serviceClass: Class<T>,
        retryOnConnectionFailure: Boolean = true
    ): T {
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
            .baseUrl("${AppConfig.HTTP_BASE_URL}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }

    // Các hàm helper để tạo từng ApiService cụ thể
    fun createAuthApiService(tokenManager: TokenManager): AuthApiService =
        createService(tokenManager, AuthApiService::class.java)

    fun createBookingApiService(tokenManager: TokenManager): BookingApiService =
        createService(tokenManager, BookingApiService::class.java)

    fun createFinanceApiService(tokenManager: TokenManager): FinanceApiService =
        createService(tokenManager, FinanceApiService::class.java)

    fun createChatApiService(tokenManager: TokenManager): ChatApiService =
        createService(tokenManager, ChatApiService::class.java)

    fun createConvenienceApiService(tokenManager: TokenManager): ConvenienceApiService =
        createService(tokenManager, ConvenienceApiService::class.java)

    fun createFeedbackApiService(tokenManager: TokenManager): FeedbackApiService =
        createService(tokenManager, FeedbackApiService::class.java)

    fun createNotificationApiService(tokenManager: TokenManager): NotificationApiService =
        createService(tokenManager, NotificationApiService::class.java)

    fun createOnboardApiService(tokenManager: TokenManager): OnboardApiService =
        createService(tokenManager, OnboardApiService::class.java)

    fun createShoreApiService(tokenManager: TokenManager): ShoreApiService =
        createService(tokenManager, ShoreApiService::class.java)

    fun createStaffApiService(tokenManager: TokenManager): StaffApiService =
        createService(tokenManager, StaffApiService::class.java)

    fun createTourApiService(tokenManager: TokenManager): TourApiService =
        createService(tokenManager, TourApiService::class.java)

    fun createPosApiService(tokenManager: TokenManager): PosApiService =
        createService(tokenManager, PosApiService::class.java)
}