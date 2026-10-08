package com.project.cruise.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import com.project.cruise.android.ui.screens.auth.RegisterScreen
import com.project.cruise.android.ui.screens.pos.PosRole
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.LoginState
import com.project.cruise.android.viewmodel.auth.RegisterState
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.auth.VerifyOtpState

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    // ----- SPLASH -----
    composable(Routes.SPLASH) {
        val sessionState by authViewModel.sessionState.collectAsState()

        LaunchedEffect(sessionState) {
            when (val state = sessionState) {
                is SessionState.Authenticated -> {
                    val role = PosRole.fromApiRole(state.role)
                    val destination =
                        if (role != null) Routes.posDashboard(role) else Routes.PASSENGER_TOURS
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                SessionState.Unauthenticated -> navController.navigate(Routes.PASSENGER_TOURS) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
                SessionState.Checking -> Unit
            }
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // ----- GUEST -----
    composable(Routes.GUEST) {
        GuestScreen(
            onLoginClick = { navController.navigate(Routes.LOGIN) },
            onRegisterClick = { navController.navigate(Routes.REGISTER) },
            onPosClick = { navController.navigate(Routes.POS_LOGIN) }
        )
    }

    // ----- LOGIN -----
    composable(Routes.LOGIN) {
        val loginState by authViewModel.loginState.collectAsState()

        LaunchedEffect(loginState) {
            if (loginState is LoginState.Success) {
                navController.navigate(Routes.PASSENGER_TOURS) {
                    popUpTo(Routes.GUEST) { inclusive = true }
                    launchSingleTop = true
                }
                authViewModel.resetLoginState()
            }
        }

        LoginScreen(
            onBackClick = { navController.popBackStack() },
            onLogin = { username, password ->
                authViewModel.login(username = username, password = password)
            },
            isLoading = loginState is LoginState.Loading,
            errorMessage = (loginState as? LoginState.Error)?.message
        )
    }

    // ----- REGISTER -----
    composable(Routes.REGISTER) {
        val registerState by authViewModel.registerState.collectAsState()

        LaunchedEffect(registerState) {
            val state = registerState
            if (state is RegisterState.Success) {
                val userId = state.response.id
                if (userId != null) {
                    navController.navigate(Routes.otp(userId)) {
                        launchSingleTop = true
                    }
                    authViewModel.resetRegisterState()
                }
            }
        }

        RegisterScreen(
            onBackClick = { navController.popBackStack() },
            onRegister = { username, password, email ->
                authViewModel.register(username = username, password = password, email = email)
            },
            isLoading = registerState is RegisterState.Loading,
            errorMessage = (registerState as? RegisterState.Error)?.message
        )
    }

    // ----- OTP -----
    composable(
        route = Routes.OTP,
        arguments = listOf(navArgument("userId") { type = NavType.LongType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
        val verifyOtpState by authViewModel.verifyOtpState.collectAsState()

        LaunchedEffect(verifyOtpState) {
            if (verifyOtpState is VerifyOtpState.Success) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
                    launchSingleTop = true
                }
                authViewModel.resetVerifyOtpState()
            }
        }

        OtpScreen(
            userId = userId,
            onBackClick = { navController.popBackStack() },
            onVerify = { id, otp -> authViewModel.verifyEmail(userId = id, otp = otp) },
            isLoading = verifyOtpState is VerifyOtpState.Loading,
            errorMessage = (verifyOtpState as? VerifyOtpState.Error)?.message
        )
    }
}