package com.project.cruise.android.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.project.cruise.android.ui.screens.passenger.MyBookingsScreen
import com.project.cruise.android.ui.screens.passenger.MyBookingDetailScreen
import com.project.cruise.android.ui.screens.TourPublicScreen
import com.project.cruise.android.ui.screens.TourDetailPublicScreen
import com.project.cruise.android.data.repository.BookingRepository
import com.project.cruise.android.data.repository.TourRepository
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModelFactory
import com.project.cruise.android.viewmodel.tour.TourViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModelFactory
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
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.auth.VerifyOtpState

object Routes {

    const val SPLASH = "splash"
    const val GUEST = "guest"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{userId}"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
    const val PASSENGER_NOTIFICATIONS = "passenger_notifications"
    const val PASSENGER_TOURS = "passenger_tours"
    const val PASSENGER_TOUR_DETAIL = "passenger_tours/{tourId}"
    const val PASSENGER_ROOMS = "passenger_rooms/{voyageId}"
    const val PASSENGER_CREATE_BOOKING = "passenger_booking/{tourId}/{packageId}"
    const val PASSENGER_BOOKINGS = "passenger_bookings"
    const val PASSENGER_BOOKING_DETAIL = "passenger_bookings/{bookingId}"
    const val PASSENGER_PAYMENT_RESULT = "passenger_payment_result?paymentId={paymentId}&status={status}"
    const val POS_DASHBOARD = "pos_dashboard"
    const val POS_QR_SCAN = "pos_qr_scan"
    const val POS_NFC_SCAN = "pos_nfc_scan"
    const val POS_HISTORY = "pos_history"
    const val POS_IDENTITY = "pos_identity/{localId}"
}

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current

    val tokenManager = remember {
        TokenManager(context.applicationContext)
    }

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

    val sessionState by viewModel.sessionState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.PASSENGER_TOURS // <-- Đặt TourPublic làm màn hình khởi đầu khi mở app
    ) {

        composable(Routes.SPLASH) {
            LaunchedEffect(sessionState) {
                when (sessionState) {
                    is SessionState.Authenticated -> navController.navigate(Routes.PASSENGER_DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                    SessionState.Unauthenticated -> navController.navigate(Routes.GUEST) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                    SessionState.Checking -> Unit
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // =================================================
        // GUEST
        // =================================================

        composable(Routes.GUEST) {
            GuestScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onPosClick = { navController.navigate(Routes.POS_DASHBOARD) }
            )
        }

        // =================================================
        // LOGIN
        // =================================================

        composable(Routes.LOGIN) {
            val loginState by viewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                if (loginState is LoginState.Success) {
                    navController.navigate(Routes.PASSENGER_DASHBOARD) {
                        popUpTo(Routes.GUEST) { inclusive = true }
                        launchSingleTop = true
                    }
                    viewModel.resetLoginState()
                }
            }

            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLogin = { username, password -> viewModel.login(username = username, password = password) },
                isLoading = loginState is LoginState.Loading,
                errorMessage = (loginState as? LoginState.Error)?.message
            )
        }

        // =================================================
        // REGISTER
        // =================================================

        composable(Routes.REGISTER) {
            val registerState by viewModel.registerState.collectAsState()

            LaunchedEffect(registerState) {
                if (registerState is RegisterState.Success) {
                    val response = (registerState as RegisterState.Success).response
                    val userId = response.id
                    if (userId != null) {
                        navController.navigate("otp/$userId") {
                            launchSingleTop = true
                        }
                        viewModel.resetRegisterState()
                    }
                }
            }

            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegister = { username, password, email ->
                    viewModel.register(username = username, password = password, email = email)
                },
                isLoading = registerState is RegisterState.Loading,
                errorMessage = (registerState as? RegisterState.Error)?.message
            )
        }

        // =================================================
        // OTP
        // =================================================

        composable(
            route = Routes.OTP,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            val verifyOtpState by viewModel.verifyOtpState.collectAsState()

            LaunchedEffect(verifyOtpState) {
                if (verifyOtpState is VerifyOtpState.Success) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                    viewModel.resetVerifyOtpState()
                }
            }

            OtpScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onVerify = { id, otp -> viewModel.verifyEmail(userId = id, otp = otp) },
                isLoading = verifyOtpState is VerifyOtpState.Loading,
                errorMessage = (verifyOtpState as? VerifyOtpState.Error)?.message
            )
        }

        // =================================================
        // PASSENGER DASHBOARD
        // =================================================

        composable(Routes.PASSENGER_DASHBOARD) {
            Dashboard(
                viewModel = viewModel,
                onBrowseTours = {
                    navController.navigate(Routes.PASSENGER_TOURS)
                },
                onMyBookings = {
                    navController.navigate(Routes.PASSENGER_BOOKINGS)
                },
                notificationButton = {},
                onLogout = {
                    navController.navigate(Routes.GUEST) {
                        popUpTo(Routes.PASSENGER_DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        // =================================================
        // PASSENGER TOURS (PUBLIC - MÀN HÌNH CHÍNH)
        // =================================================

        composable(Routes.PASSENGER_TOURS) {
            val tourRepo = remember(apiService) { TourRepository(apiService) }
            val tourViewModel: TourViewModel = viewModel(
                factory = TourViewModelFactory(tourRepo)
            )

            TourPublicScreen(
                viewModel = tourViewModel,
                onTourClick = { tourId ->
                    navController.navigate("passenger_tours/$tourId")
                },
                onAuthClick = {
                    navController.navigate(Routes.GUEST) // Bấm nút "Tài khoản" sẽ chuyển sang màn hình Guest
                }
            )
        }

        // =================================================
        // PASSENGER TOUR DETAIL (PUBLIC)
        // =================================================

        composable(
            route = Routes.PASSENGER_TOUR_DETAIL,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: return@composable
            val tourRepo = remember(apiService) { TourRepository(apiService) }
            val tourViewModel: TourViewModel = viewModel(
                factory = TourViewModelFactory(tourRepo)
            )

            val isLoggedIn = sessionState is SessionState.Authenticated

            TourDetailPublicScreen(
                tourId = tourId,
                viewModel = tourViewModel,
                isLoggedIn = isLoggedIn,
                onBookClick = { packageId ->
                    navController.navigate("passenger_booking/$tourId/$packageId")
                },
                onLoginRequired = {
                    navController.navigate(Routes.LOGIN)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // =================================================
        // PASSENGER BOOKINGS LIST
        // =================================================

        composable(Routes.PASSENGER_BOOKINGS) {
            val bookingRepo = remember(apiService) { BookingRepository(apiService) }
            val bookingViewModel: BookingViewModel = viewModel(
                factory = BookingViewModelFactory(bookingRepo)
            )

            MyBookingsScreen(
                viewModel = bookingViewModel,
                onBookingClick = { bookingId ->
                    navController.navigate("passenger_bookings/$bookingId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // =================================================
        // PASSENGER BOOKING DETAIL
        // =================================================

        composable(
            route = Routes.PASSENGER_BOOKING_DETAIL,
            arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: return@composable
            val bookingRepo = remember(apiService) { BookingRepository(apiService) }
            val bookingViewModel: BookingViewModel = viewModel(
                factory = BookingViewModelFactory(bookingRepo)
            )

            MyBookingDetailScreen(
                bookingId = bookingId,
                viewModel = bookingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}