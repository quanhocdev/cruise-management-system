package com.project.cruise.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.network.api.AuthApiService
import com.project.cruise.android.data.repository.auth.AuthRepository
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory
import com.project.cruise.android.data.repository.chat.ChatRepositoryProvider

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val tokenManager = remember { TokenManager(context.applicationContext) }

    // Sử dụng AuthApiService thay cho ApiService chung cũ
    val authApiService: AuthApiService = remember(tokenManager) {
        RetrofitClient.createAuthApiService(tokenManager)
    }

    val chatRepository = remember(context.applicationContext) {
        ChatRepositoryProvider.initialize(context.applicationContext)
        ChatRepositoryProvider.getInstance()
    }

    val authRepository = remember(authApiService, tokenManager) {
        AuthRepository(apiService = authApiService, tokenManager = tokenManager)
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        authGraph(navController, authViewModel)

        // Lưu ý: passengerGraph nếu cần các ApiService riêng, bạn có thể truyền trực tiếp các service tương ứng (BookingApiService, TourApiService, v.v.)
        passengerGraph(navController, authViewModel, tokenManager, chatRepository)

        posGraph(navController, authViewModel)
        staffGraph(navController, authViewModel, tokenManager)
    }
}