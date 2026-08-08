package com.project.cruise.android.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.repository.AuthRepository

import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import com.project.cruise.android.ui.screens.auth.RegisterScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard

import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory
import com.project.cruise.android.viewmodel.auth.LoginState
import com.project.cruise.android.viewmodel.auth.RegisterState
import com.project.cruise.android.viewmodel.auth.VerifyOtpState

object Routes {

    const val GUEST = "guest"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{userId}"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
}

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    // =====================================================
    // TOKEN MANAGER
    // =====================================================

    val context = androidx.compose.ui.platform.LocalContext.current

    val tokenManager = remember {
        TokenManager(context.applicationContext)
    }

    // =====================================================
    // NETWORK
    // =====================================================

    val apiService: ApiService = remember(tokenManager) {
        RetrofitClient.createApiService(tokenManager)
    }

    val repository = remember(apiService, tokenManager) {
        AuthRepository(
            apiService = apiService,
            tokenManager = tokenManager
        )
    }

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.GUEST
    ) {

        // =================================================
        // GUEST
        // =================================================

        composable(Routes.GUEST) {

            GuestScreen(

                onLoginClick = {
                    navController.navigate(
                        Routes.LOGIN
                    )
                },

                onRegisterClick = {
                    navController.navigate(
                        Routes.REGISTER
                    )
                }
            )
        }

        // =================================================
        // LOGIN
        // =================================================

        composable(Routes.LOGIN) {

            val loginState by
            viewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {

                if (loginState is LoginState.Success) {

                    navController.navigate(
                        Routes.PASSENGER_DASHBOARD
                    ) {

                        popUpTo(Routes.GUEST) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }

                    viewModel.resetLoginState()
                }
            }

            LoginScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLogin = { username, password ->

                    viewModel.login(
                        username = username,
                        password = password
                    )
                },

                isLoading =
                    loginState is LoginState.Loading,

                errorMessage =
                    (loginState as? LoginState.Error)?.message
            )
        }

        // =================================================
        // REGISTER
        // =================================================

        composable(Routes.REGISTER) {

            val registerState by
            viewModel.registerState.collectAsState()

            LaunchedEffect(registerState) {

                if (registerState is RegisterState.Success) {

                    val response =
                        (registerState as RegisterState.Success)
                            .response

                    val userId = response.id

                    if (userId != null) {

                        navController.navigate(
                            "otp/$userId"
                        ) {
                            launchSingleTop = true
                        }

                        viewModel.resetRegisterState()
                    }
                }
            }

            RegisterScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onRegister = {
                        username,
                        password,
                        email ->

                    viewModel.register(
                        username = username,
                        password = password,
                        email = email
                    )
                },

                isLoading =
                    registerState is RegisterState.Loading,

                errorMessage =
                    (registerState as? RegisterState.Error)?.message
            )
        }

        // =================================================
        // OTP
        // =================================================

        composable(
            route = Routes.OTP,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val userId =
                backStackEntry
                    .arguments
                    ?.getLong("userId")

            if (userId == null) {
                return@composable
            }

            val verifyOtpState by
            viewModel.verifyOtpState.collectAsState()

            LaunchedEffect(verifyOtpState) {

                if (
                    verifyOtpState
                            is VerifyOtpState.Success
                ) {

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(
                            Routes.REGISTER
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }

                    viewModel.resetVerifyOtpState()
                }
            }

            OtpScreen(

                userId = userId,

                onBackClick = {
                    navController.popBackStack()
                },

                onVerify = { id, otp ->

                    viewModel.verifyEmail(
                        userId = id,
                        otp = otp
                    )
                },

                isLoading =
                    verifyOtpState is VerifyOtpState.Loading,

                errorMessage =
                    (verifyOtpState as? VerifyOtpState.Error)?.message
            )
        }

        // =================================================
        // PASSENGER
        // =================================================

        composable(
            Routes.PASSENGER_DASHBOARD
        ) {

            Dashboard(
                viewModel = viewModel
            )
        }
    }
}