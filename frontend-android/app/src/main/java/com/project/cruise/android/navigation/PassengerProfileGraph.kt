package com.project.cruise.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.project.cruise.android.data.repository.notification.ActivityInboxRepository
import com.project.cruise.android.ui.passenger.screen.Dashboard
import com.project.cruise.android.ui.passenger.screen.ProfileScreen
import com.project.cruise.android.ui.screens.passenger.ActivityInboxButton
import com.project.cruise.android.viewmodel.auth.AuthViewModel

fun NavGraphBuilder.passengerProfileGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {

    // ----- PROFILE -----
    composable(Routes.PASSENGER_PROFILE) {
        ProfileScreen(
            authViewModel = authViewModel,

            onHomeClick = {
                navController.navigate(Routes.PASSENGER_TOURS) {
                    popUpTo(Routes.PASSENGER_TOURS) {
                        inclusive = true
                    }
                }
            },

            onMyBookingsClick = {
                navController.navigate(Routes.PASSENGER_BOOKINGS)
            },

            onMessagesClick = {
                navController.navigate(Routes.PASSENGER_MESSAGES)
            },

            // MỞ TRANG THÔNG BÁO
            onNotificationClick = {
                navController.navigate(Routes.PASSENGER_NOTIFICATIONS) {
                    launchSingleTop = true
                }
            },

            onMyFeedbackClick = {
                navController.navigate(Routes.PASSENGER_FEEDBACK)
            },

            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_TOURS) {
                        inclusive = true
                    }
                }
            }
        )
    }

    // ----- DASHBOARD -----
    composable(Routes.PASSENGER_DASHBOARD) {
        Dashboard(
            viewModel = authViewModel,
            onBrowseTours = { navController.navigate(Routes.PASSENGER_TOURS) },
            onMyBookings = { navController.navigate(Routes.PASSENGER_BOOKINGS) },
            notificationButton = { ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) } },
            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_DASHBOARD) { inclusive = true }
                }
            }
        )
    }
}

/** Nút hộp thư hoạt động, dùng chung cho nhiều màn passenger. */
@Composable
internal fun ActivityInboxEntry(onClick: () -> Unit) {
    val context = LocalContext.current
    val source = remember(context) { ActivityInboxRepository(context) }
    ActivityInboxButton(source, onClick)
}