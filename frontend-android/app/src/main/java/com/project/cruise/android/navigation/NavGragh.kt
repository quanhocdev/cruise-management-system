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

import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.repository.AuthRepository
import com.project.cruise.android.data.repository.PosIdentityRepository
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModel
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModelFactory
import com.project.cruise.android.ui.screens.pos.PosIdentityScreen
import com.project.cruise.android.data.repository.PassengerCatalogRepository
import com.project.cruise.android.data.repository.PassengerBookingRepository
import com.project.cruise.android.ui.screens.passenger.CreateBookingScreen
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModelFactory

import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import com.project.cruise.android.ui.screens.auth.RegisterScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard
import com.project.cruise.android.ui.screens.passenger.AvailableRoomsScreen
import com.project.cruise.android.ui.screens.passenger.TourDetailScreen
import com.project.cruise.android.ui.screens.passenger.TourListScreen
import com.project.cruise.android.ui.screens.pos.PosDashboardScreen
import com.project.cruise.android.ui.screens.pos.QrScanScreen
import com.project.cruise.android.ui.screens.pos.NfcScanScreen
import com.project.cruise.android.ui.screens.pos.PosHistoryScreen

import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.AuthViewModelFactory
import com.project.cruise.android.viewmodel.auth.LoginState
import com.project.cruise.android.viewmodel.auth.RegisterState
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.auth.VerifyOtpState
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModelFactory

object Routes {

    const val SPLASH = "splash"
    const val GUEST = "guest"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{userId}"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
    const val PASSENGER_TOURS = "passenger_tours"
    const val PASSENGER_TOUR_DETAIL = "passenger_tours/{tourId}"
    const val PASSENGER_ROOMS = "passenger_rooms/{voyageId}"
    const val PASSENGER_CREATE_BOOKING = "passenger_booking/{voyageId}/{roomId}"
    const val POS_DASHBOARD = "pos_dashboard"
    const val POS_QR_SCAN = "pos_qr_scan"
    const val POS_NFC_SCAN = "pos_nfc_scan"
    const val POS_HISTORY = "pos_history"
    const val POS_IDENTITY = "pos_identity/{localId}"
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

    val catalogViewModel: PassengerCatalogViewModel = viewModel(
        factory = PassengerCatalogViewModelFactory(remember(apiService) { PassengerCatalogRepository(apiService) })
    )

    val sessionState by viewModel.sessionState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
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

                onLoginClick = {
                    navController.navigate(
                        Routes.LOGIN
                    )
                },

                onRegisterClick = {
                    navController.navigate(
                        Routes.REGISTER
                    )
                },

                onPosClick = {
                    navController.navigate(Routes.POS_DASHBOARD)
                }
            )
        }

        composable(Routes.POS_DASHBOARD) {
            PosDashboardScreen(
                onBackClick = { navController.popBackStack() },
                onQrClick = { navController.navigate(Routes.POS_QR_SCAN) },
                onNfcClick = { navController.navigate(Routes.POS_NFC_SCAN) },
                onHistoryClick = { navController.navigate(Routes.POS_HISTORY) }
            )
        }

        composable(Routes.POS_QR_SCAN) {
            QrScanScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { id ->
                    navController.navigate("pos_identity/$id") {
                        popUpTo(Routes.POS_QR_SCAN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.POS_NFC_SCAN) {
            NfcScanScreen(
                onBackClick = { navController.popBackStack() },
                onSaved = { id ->
                    navController.navigate("pos_identity/$id") {
                        popUpTo(Routes.POS_NFC_SCAN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.POS_HISTORY) {
            PosHistoryScreen(onBackClick = { navController.popBackStack() },
                onIdentify = { navController.navigate("pos_identity/$it") })
        }

        composable(Routes.POS_IDENTITY, arguments = listOf(navArgument("localId") { type = NavType.StringType })) { entry ->
            val identityViewModel: PosIdentityViewModel = viewModel(
                viewModelStoreOwner = entry,
                factory = PosIdentityViewModelFactory(PosIdentityRepository(context), entry.arguments?.getString("localId").orEmpty())
            )
            val identityState by identityViewModel.state.collectAsState()
            PosIdentityScreen(identityState, onRetry = identityViewModel::verify,
                onCheckIn = identityViewModel::checkIn, onBack = { navController.popBackStack() })
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
                viewModel = viewModel,
                onBrowseTours = { navController.navigate(Routes.PASSENGER_TOURS) },
                onLogout = {
                    // 🟢 Điều hướng về màn hình Login và xóa sạch lịch sử Navigation (Backstack)
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.PASSENGER_TOURS) {
            TourListScreen(
                viewModel = catalogViewModel,
                onBack = { navController.popBackStack() },
                onTourClick = { navController.navigate("passenger_tours/$it") }
            )
        }

        composable(
            Routes.PASSENGER_TOUR_DETAIL,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { entry ->
            TourDetailScreen(
                tourId = entry.arguments?.getString("tourId").orEmpty(),
                viewModel = catalogViewModel,
                onBack = { navController.popBackStack() },
                onDepartureClick = { navController.navigate("passenger_rooms/$it") }
            )
        }

        composable(
            Routes.PASSENGER_ROOMS,
            arguments = listOf(navArgument("voyageId") { type = NavType.StringType })
        ) { entry ->
            AvailableRoomsScreen(
                voyageId = entry.arguments?.getString("voyageId").orEmpty(),
                viewModel = catalogViewModel,
                onBack = { navController.popBackStack() },
                onSelectRoom = { roomId ->
                    val voyageId = entry.arguments?.getString("voyageId").orEmpty()
                    navController.navigate("passenger_booking/$voyageId/$roomId") { launchSingleTop = true }
                }
            )
        }

        composable(
            Routes.PASSENGER_CREATE_BOOKING,
            arguments = listOf(
                navArgument("voyageId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { entry ->
            val bookingViewModel: PassengerBookingViewModel = viewModel(
                viewModelStoreOwner = entry,
                factory = PassengerBookingViewModelFactory(
                    PassengerBookingRepository(remember(tokenManager) {
                        RetrofitClient.createApiService(tokenManager, retryOnConnectionFailure = false)
                    }),
                    entry.arguments?.getString("voyageId").orEmpty(),
                    entry.arguments?.getString("roomId").orEmpty()
                )
            )
            val bookingState by bookingViewModel.state.collectAsState()
            CreateBookingScreen(
                state = bookingState,
                onDraftChange = bookingViewModel::edit,
                onSubmit = bookingViewModel::submit,
                onRetry = bookingViewModel::refreshRoom,
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.navigate(Routes.PASSENGER_DASHBOARD) {
                        popUpTo(Routes.PASSENGER_DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
