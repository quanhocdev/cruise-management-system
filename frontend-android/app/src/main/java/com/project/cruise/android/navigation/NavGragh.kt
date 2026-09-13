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
import com.project.cruise.android.ui.screens.ProfileScreen
import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import com.project.cruise.android.ui.screens.auth.RegisterScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard

import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory
import com.project.cruise.android.viewmodel.auth.LoginState
import com.project.cruise.android.viewmodel.auth.PosLoginState
import com.project.cruise.android.viewmodel.auth.RegisterState
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.auth.VerifyOtpState
import com.project.cruise.android.ui.screens.pos.PosDashboardScreen
import com.project.cruise.android.ui.screens.pos.PosLoginScreen
import com.project.cruise.android.ui.screens.pos.PosManualEntryScreen
import com.project.cruise.android.ui.screens.pos.PosRole
import com.project.cruise.android.ui.screens.pos.QrScanScreen
import com.project.cruise.android.ui.screens.pos.NfcScanScreen
import com.project.cruise.android.ui.screens.pos.PosHistoryScreen
import com.project.cruise.android.ui.screens.pos.PosIdentityScreen
import com.project.cruise.android.data.repository.PosIdentityRepository
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModel
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModelFactory
object Routes {

    const val SPLASH = "splash"
    const val GUEST = "guest"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{userId}"

    const val PASSENGER_PROFILE = "passenger_profile"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
    const val PASSENGER_NOTIFICATIONS = "passenger_notifications"
    const val PASSENGER_TOURS = "passenger_tours"
    const val PASSENGER_TOUR_DETAIL = "passenger_tours/{tourId}"
    const val PASSENGER_ROOMS = "passenger_rooms/{voyageId}"
    const val PASSENGER_CREATE_BOOKING = "passenger_booking/{tourId}/{packageId}"
    const val PASSENGER_BOOKINGS = "passenger_bookings"
    const val PASSENGER_BOOKING_DETAIL = "passenger_bookings/{bookingId}"
    const val PASSENGER_PAYMENT_RESULT = "passenger_payment_result?paymentId={paymentId}&status={status}"
    const val POS_LOGIN = "pos_login"
    const val POS_DASHBOARD = "pos_dashboard/{role}"
    const val POS_QR_SCAN = "pos_qr_scan/{role}"
    const val POS_NFC_SCAN = "pos_nfc_scan/{role}"
    const val POS_MANUAL_ENTRY = "pos_manual_entry/{role}"
    const val POS_HISTORY = "pos_history/{role}"
    const val POS_IDENTITY = "pos_identity/{role}/{localId}"

    fun posDashboard(role: PosRole) = "pos_dashboard/${role.apiRole}"
    fun posQrScan(role: PosRole) = "pos_qr_scan/${role.apiRole}"
    fun posNfcScan(role: PosRole) = "pos_nfc_scan/${role.apiRole}"
    fun posManualEntry(role: PosRole) = "pos_manual_entry/${role.apiRole}"
    fun posHistory(role: PosRole) = "pos_history/${role.apiRole}"
    fun posIdentity(role: PosRole, localId: String) = "pos_identity/${role.apiRole}/$localId"
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
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            LaunchedEffect(sessionState) {
                when (sessionState) {
                    is SessionState.Authenticated -> {
                        val role = PosRole.fromApiRole((sessionState as SessionState.Authenticated).role)
                        navController.navigate(role?.let(Routes::posDashboard) ?: Routes.PASSENGER_TOURS) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
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
                onPosClick = { navController.navigate(Routes.POS_LOGIN) }
            )
        }

        // =================================================
        // LOGIN
        // =================================================

        composable(Routes.LOGIN) {
            val loginState by viewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                if (loginState is LoginState.Success) {
                    navController.navigate(Routes.PASSENGER_TOURS) {
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
        // PASSENGER PROFILE
        // =================================================
        composable(Routes.PASSENGER_PROFILE) {
            ProfileScreen(
                authViewModel = viewModel,
                onHomeClick = {
                    navController.navigate(Routes.PASSENGER_TOURS) {
                        popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                    }
                },
                onMyBookingsClick = {
                    navController.navigate(Routes.PASSENGER_BOOKINGS)
                },
                onLogout = {
                    navController.navigate(Routes.GUEST) {
                        popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                    }
                },
                onNotificationClick = {
                    // Xử lý chuyển sang trang thông báo nếu có
                }
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
                authViewModel = viewModel,
                onTourClick = { tourId ->
                    navController.navigate("passenger_tours/$tourId")
                },
                onLoginClick = {
                    navController.navigate(Routes.GUEST) // Chưa đăng nhập -> sang Guest
                },
                onUserClick = {
                    navController.navigate(Routes.PASSENGER_PROFILE) // Đã đăng nhập -> sang Profile
                },
                onMyBookingsClick = {
                    navController.navigate(Routes.PASSENGER_BOOKINGS)
                },
                onLogout = {
                    navController.navigate(Routes.GUEST) {
                        popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                    }
                },
                onHomeClick = {
                    // Đang ở trang chủ rồi nên giữ nguyên hoặc làm mới
                },
                onNotificationClick = {
                    // Chuyển sang màn hình thông báo sau
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
        // =================================================
        // POS - ĐĂNG NHẬP VÀ PHÂN QUYỀN THEO ROLE
        // =================================================

        composable(Routes.POS_LOGIN) {
            val posLoginState by viewModel.posLoginState.collectAsState()

            LaunchedEffect(posLoginState) {
                val success = posLoginState as? PosLoginState.Success ?: return@LaunchedEffect
                val role = PosRole.fromApiRole(success.response.role) ?: return@LaunchedEffect
                navController.navigate(Routes.posDashboard(role)) {
                    popUpTo(Routes.GUEST) { inclusive = true }
                    launchSingleTop = true
                }
                viewModel.resetPosLoginState()
            }

            PosLoginScreen(
                onBackClick = { navController.popBackStack() },
                onLogin = viewModel::loginPos,
                isLoading = posLoginState is PosLoginState.Loading,
                errorMessage = (posLoginState as? PosLoginState.Error)?.message
            )
        }

        composable(
            route = Routes.POS_DASHBOARD,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            PosDashboardScreen(
                role = role,
                onLogoutClick = {
                    viewModel.logout {
                        navController.navigate(Routes.GUEST) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                },
                onQrClick = { navController.navigate(Routes.posQrScan(role)) },
                onNfcClick = { navController.navigate(Routes.posNfcScan(role)) },
                onManualClick = { navController.navigate(Routes.posManualEntry(role)) },
                onHistoryClick = { navController.navigate(Routes.posHistory(role)) }
            )
        }

        composable(
            route = Routes.POS_QR_SCAN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            QrScanScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { localId ->
                    navController.navigate(Routes.posIdentity(role, localId)) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )
        }

        composable(
            route = Routes.POS_NFC_SCAN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            NfcScanScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { localId ->
                    navController.navigate(Routes.posIdentity(role, localId)) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )
        }

        composable(
            route = Routes.POS_MANUAL_ENTRY,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            PosManualEntryScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { localId -> navController.navigate(Routes.posIdentity(role, localId)) }
            )
        }

        composable(
            route = Routes.POS_HISTORY,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            PosHistoryScreen(
                role = role,
                onBackClick = { navController.popBackStack() },
                onIdentify = { localId ->
                    navController.navigate(Routes.posIdentity(role, localId))
                }
            )
        }

        composable(
            route = Routes.POS_IDENTITY,
            arguments = listOf(
                navArgument("role") { type = NavType.StringType },
                navArgument("localId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val role = PosRole.fromApiRole(backStackEntry.arguments?.getString("role"))
                ?: return@composable
            val localId = backStackEntry.arguments?.getString("localId") ?: return@composable

            val repository = remember(context) { PosIdentityRepository(context) }
            val posViewModel: PosIdentityViewModel = viewModel(
                factory = PosIdentityViewModelFactory(repository, localId)
            )
            val state by posViewModel.state.collectAsState()

            PosIdentityScreen(
                role = role,
                state = state,
                onRetry = { posViewModel.verify() },
                onCheckIn = { posViewModel.checkIn() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
