
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
import com.project.cruise.android.data.repository.chat.ChatRepositoryProvider
import com.project.cruise.android.data.repository.feedback.FeedbackRepository

import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val tokenManager = remember {
        TokenManager(context.applicationContext)
    }

    // Auth API
    val authApiService: AuthApiService = remember(tokenManager) {
        RetrofitClient.createAuthApiService(tokenManager)
    }

    // Chat Repository
    val chatRepository = remember(context.applicationContext) {
        ChatRepositoryProvider.initialize(context.applicationContext)
        ChatRepositoryProvider.getInstance()
    }

    // Auth Repository
    val authRepository = remember(authApiService, tokenManager) {
        AuthRepository(
            apiService = authApiService,
            tokenManager = tokenManager
        )
    }

    // Auth ViewModel
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    // =========================================
    // FEEDBACK
    // =========================================

    // Feedback API
    val feedbackApiService = remember(tokenManager) {
        RetrofitClient.createFeedbackApiService(tokenManager)
    }

    // Feedback Repository
    val feedbackRepository = remember(feedbackApiService) {
        FeedbackRepository(feedbackApiService)
    }

    // Feedback ViewModel
    val feedbackViewModel: FeedbackViewModel = viewModel(
        factory = FeedbackViewModel.Factory(feedbackRepository)
    )

    // =========================================
    // NAVIGATION
    // =========================================

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        authGraph(
            navController,
            authViewModel
        )

        passengerGraph(
            navController = navController,
            authViewModel = authViewModel,
            tokenManager = tokenManager,
            chatRepository = chatRepository,
            feedbackViewModel = feedbackViewModel
        )

        posGraph(
            navController,
            authViewModel
        )

        staffGraph(
            navController,
            authViewModel,
            tokenManager
        )
    }
}
