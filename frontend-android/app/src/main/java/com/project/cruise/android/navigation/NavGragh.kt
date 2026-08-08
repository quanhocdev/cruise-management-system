package com.project.cruise.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import com.project.cruise.android.ui.screens.auth.RegisterScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard

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

    NavHost(
        navController = navController,
        startDestination = Routes.GUEST
    ) {

        // ==========================
        // GUEST
        // ==========================

        composable(Routes.GUEST) {

            GuestScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ==========================
        // LOGIN
        // ==========================

        composable(Routes.LOGIN) {

            LoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginSuccess = {

                    navController.navigate(
                        Routes.PASSENGER_DASHBOARD
                    ) {
                        popUpTo(Routes.GUEST) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ==========================
        // REGISTER
        // ==========================

        composable(Routes.REGISTER) {

            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegister = { _, _, _ ->

                    // Tạm thời để test navigation.
                    // Sau này gọi AuthViewModel.register()
                    // rồi lấy userId từ RegisterResponse.

                    val userId = 1L

                    navController.navigate(
                        "otp/$userId"
                    )
                }
            )
        }

        // ==========================
        // OTP
        // ==========================

        composable(
            route = Routes.OTP,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val userId =
                backStackEntry.arguments?.getLong("userId") ?: return@composable

            OtpScreen(
                userId = userId,

                onBackClick = {
                    navController.popBackStack()
                },

                onVerify = { _, _ ->

                    // Tạm thời test navigation.
                    // Sau này gọi AuthViewModel.verifyEmail()

                    navController.navigate(
                        Routes.LOGIN
                    ) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ==========================
        // PASSENGER
        // ==========================

        composable(Routes.PASSENGER_DASHBOARD) {

            Dashboard()
        }
    }
}