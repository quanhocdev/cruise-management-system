package com.project.cruise.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.repository.AuthRepository
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val tokenManager = remember { TokenManager(context.applicationContext) }

    val apiService: ApiService = remember(tokenManager) {
        RetrofitClient.createApiService(tokenManager)
    }

    val authRepository = remember(apiService, tokenManager) {
        AuthRepository(apiService = apiService, tokenManager = tokenManager)
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        authGraph(navController, authViewModel)
        passengerGraph(navController, authViewModel, apiService)
        posGraph(navController, authViewModel)
    }
}