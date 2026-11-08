package com.project.cruise.android.data.repository.chat

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.AppConfig
import com.project.cruise.android.data.network.ChatSocketService
import com.project.cruise.android.data.network.RetrofitClient

object ChatRepositoryProvider {

    @Volatile
    private var instance: ChatRepository? = null

    fun initialize(context: Context) {
        if (instance != null) return

        synchronized(this) {
            if (instance != null) return

            val appContext = context.applicationContext

            val tokenManager = TokenManager(appContext)

            // Đổi sang createChatApiService
            val apiService = RetrofitClient.createChatApiService(
                tokenManager = tokenManager
            )

            val chatSocketService = ChatSocketService()

            instance = ChatRepository(
                apiService = apiService,
                tokenManager = tokenManager,
                chatSocketService = chatSocketService,
                baseUrl = AppConfig.HTTP_BASE_URL
            )
        }
    }

    fun getInstance(): ChatRepository {
        return checkNotNull(instance) {
            "ChatRepository chưa được khởi tạo. Hãy gọi initialize() trước."
        }
    }
}