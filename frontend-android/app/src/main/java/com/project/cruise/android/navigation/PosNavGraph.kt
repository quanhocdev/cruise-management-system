package com.project.cruise.android.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.cruise.android.data.repository.PosIdentityRepository
import com.project.cruise.android.ui.screens.pos.NfcScanScreen
import com.project.cruise.android.ui.screens.pos.PosDashboardScreen
import com.project.cruise.android.ui.screens.pos.PosHistoryScreen
import com.project.cruise.android.ui.screens.pos.PosIdentityScreen
import com.project.cruise.android.ui.screens.pos.PosLoginScreen
import com.project.cruise.android.ui.screens.pos.PosManualEntryScreen
import com.project.cruise.android.ui.screens.pos.PosRole
import com.project.cruise.android.ui.screens.pos.QrScanScreen
import com.project.cruise.android.ui.screens.pos.convenience.ConvenienceNfcScanScreen
import com.project.cruise.android.ui.screens.pos.convenience.ConvenienceUsageScreen
import com.project.cruise.android.ui.screens.pos.onboard.OnboardNfcScanScreen
import com.project.cruise.android.ui.screens.pos.onboard.OnboardUsageScreen
import com.project.cruise.android.ui.screens.pos.shore.ShoreNfcScanScreen
import com.project.cruise.android.ui.screens.pos.shore.ShoreUsageScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.PosLoginState
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModel
import com.project.cruise.android.viewmodel.pos.PosIdentityViewModelFactory

fun NavGraphBuilder.posGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    // ----- LOGIN & PHÂN QUYỀN THEO ROLE -----
    composable(Routes.POS_LOGIN) {
        val posLoginState by authViewModel.posLoginState.collectAsState()

        LaunchedEffect(posLoginState) {
            val success = posLoginState as? PosLoginState.Success ?: return@LaunchedEffect
            val role = PosRole.fromApiRole(success.response.role) ?: return@LaunchedEffect
            navController.navigate(Routes.posDashboard(role)) {
                popUpTo(Routes.GUEST) { inclusive = true }
                launchSingleTop = true
            }
            authViewModel.resetPosLoginState()
        }

        PosLoginScreen(
            onBackClick = { navController.popBackStack() },
            onLogin = authViewModel::loginPos,
            isLoading = posLoginState is PosLoginState.Loading,
            errorMessage = (posLoginState as? PosLoginState.Error)?.message
        )
    }

    // ----- DASHBOARD -----
    composable(Routes.POS_DASHBOARD, arguments = posRoleArguments) { entry ->
        val role = entry.posRole() ?: return@composable
        PosDashboardScreen(
            role = role,
            onLogoutClick = {
                authViewModel.logout {
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

    // ----- QR SCAN -----
    composable(Routes.POS_QR_SCAN, arguments = posRoleArguments) { entry ->
        val role = entry.posRole() ?: return@composable
        QrScanScreen(
            role = role,
            onBackClick = { navController.popBackStack() },
            onSaved = { localId ->
                navController.navigate(Routes.posIdentity(role, localId)) {
                    popUpTo(Routes.posDashboard(role))
                }
            }
        )
    }

    // ----- NFC SCAN (mỗi role một màn hình riêng) -----
    composable(Routes.POS_NFC_SCAN, arguments = posRoleArguments) { entry ->
        val role = entry.posRole() ?: return@composable

        when (role) {
            PosRole.CONVENIENCE -> ConvenienceNfcScanScreen(
                onBackClick = { navController.popBackStack() },
                onResolved = { response, nfcCardUid ->
                    navController.navigate(
                        Routes.posConvenienceUsage(
                            bookingPassengerId = response.bookingPassengerId,
                            passengerName = response.passengerName,
                            bookingId = response.bookingId,
                            tourId = response.tourId,
                            tourPackageId = response.tourPackageId,
                            nfcCardUid = nfcCardUid
                        )
                    ) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )

            PosRole.ONBOARD -> OnboardNfcScanScreen(
                onBackClick = { navController.popBackStack() },
                onResolved = { response, nfcCardUid ->
                    navController.navigate(
                        Routes.posOnboardUsage(
                            bookingPassengerId = response.bookingPassengerId,
                            passengerName = response.passengerName,
                            bookingId = response.bookingId,
                            tourId = response.tourId,
                            tourPackageId = response.tourPackageId,
                            nfcCardUid = nfcCardUid
                        )
                    ) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )

            PosRole.SHORE -> ShoreNfcScanScreen(
                onBackClick = { navController.popBackStack() },
                onResolved = { response, nfcCardUid ->
                    navController.navigate(
                        Routes.posShoreUsage(
                            bookingPassengerId = response.bookingPassengerId,
                            passengerName = response.passengerName,
                            bookingId = response.bookingId,
                            tourId = response.tourId,
                            tourPackageId = response.tourPackageId,
                            nfcCardUid = nfcCardUid
                        )
                    ) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )

            else -> NfcScanScreen(
                role = role,
                onBackClick = { navController.popBackStack() },
                onSaved = { localId ->
                    navController.navigate(Routes.posIdentity(role, localId)) {
                        popUpTo(Routes.posDashboard(role))
                    }
                }
            )
        }
    }

    // ----- USAGE (convenience / onboard / shore) -----
    composable(Routes.POS_CONVENIENCE_USAGE, arguments = usageArguments) { entry ->
        val args = entry.toUsageArgs() ?: return@composable
        ConvenienceUsageScreen(
            bookingPassengerId = args.bookingPassengerId,
            passengerName = args.passengerName,
            bookingId = args.bookingId,
            tourId = args.tourId,
            tourPackageId = args.tourPackageId,
            nfcCardUid = args.nfcCardUid,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(Routes.POS_ONBOARD_USAGE, arguments = usageArguments) { entry ->
        val args = entry.toUsageArgs() ?: return@composable
        OnboardUsageScreen(
            bookingPassengerId = args.bookingPassengerId,
            passengerName = args.passengerName,
            bookingId = args.bookingId,
            tourId = args.tourId,
            tourPackageId = args.tourPackageId,
            nfcCardUid = args.nfcCardUid,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(Routes.POS_SHORE_USAGE, arguments = usageArguments) { entry ->
        val args = entry.toUsageArgs() ?: return@composable
        ShoreUsageScreen(
            bookingPassengerId = args.bookingPassengerId,
            passengerName = args.passengerName,
            bookingId = args.bookingId,
            tourId = args.tourId,
            tourPackageId = args.tourPackageId,
            nfcCardUid = args.nfcCardUid,
            onBackClick = { navController.popBackStack() }
        )
    }

    // ----- MANUAL ENTRY -----
    composable(Routes.POS_MANUAL_ENTRY, arguments = posRoleArguments) { entry ->
        val role = entry.posRole() ?: return@composable
        PosManualEntryScreen(
            role = role,
            onBackClick = { navController.popBackStack() },
            onSaved = { localId -> navController.navigate(Routes.posIdentity(role, localId)) }
        )
    }

    // ----- HISTORY -----
    composable(Routes.POS_HISTORY, arguments = posRoleArguments) { entry ->
        val role = entry.posRole() ?: return@composable
        PosHistoryScreen(
            role = role,
            onBackClick = { navController.popBackStack() },
            onIdentify = { localId -> navController.navigate(Routes.posIdentity(role, localId)) }
        )
    }

    // ----- IDENTITY -----
    composable(
        route = Routes.POS_IDENTITY,
        arguments = listOf(
            navArgument("role") { type = NavType.StringType },
            navArgument("localId") { type = NavType.StringType }
        )
    ) { entry ->
        val role = entry.posRole() ?: return@composable
        val localId = entry.arguments?.getString("localId") ?: return@composable

        val context = LocalContext.current
        val repository = remember(context, role) {
            PosIdentityRepository(context, role.apiRole)
        }
        val posViewModel: PosIdentityViewModel = viewModel(
            factory = PosIdentityViewModelFactory(repository, localId)
        )
        val state by posViewModel.state.collectAsState()

        PosIdentityScreen(
            role = role,
            state = state,
            onRetry = { posViewModel.sendScan() },
            onBack = { navController.popBackStack() }
        )
    }
}