package com.project.cruise.android.navigation

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
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.feedback.FeedbackRepository
import com.project.cruise.android.data.repository.notification.ActivityInboxRepository
import com.project.cruise.android.data.repository.notification.NotificationRepository
import com.project.cruise.android.ui.passenger.screen.MyFeedbackScreen
import com.project.cruise.android.ui.screens.passenger.ActivityInboxScreen
import com.project.cruise.android.ui.screens.passenger.NotificationScreen
import com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel
import com.project.cruise.android.viewmodel.passenger.NotificationViewModel

fun NavGraphBuilder.passengerNotificationGraph(
    navController: NavHostController,
    tokenManager: TokenManager,
) {

    // ----- NOTIFICATIONS SCREEN -----
    composable(Routes.PASSENGER_NOTIFICATIONS) { backStackEntry ->
        val toursEntry = remember(backStackEntry) {
            runCatching { navController.getBackStackEntry(Routes.PASSENGER_TOURS) }
                .getOrDefault(backStackEntry)
        }
        val notificationRepo = remember(tokenManager) {
            NotificationRepository(RetrofitClient.createNotificationApiService(tokenManager))
        }
        val notificationViewModel: NotificationViewModel = viewModel(
            viewModelStoreOwner = toursEntry,
            factory = NotificationViewModel.Factory(
                repository = notificationRepo,
                tokenManager = tokenManager
            )
        )
        val notificationState by notificationViewModel.state.collectAsState()

        NotificationScreen(
            state = notificationState,
            onRefresh = { notificationViewModel.loadInitialData() },
            onMarkAsRead = { notificationId ->
                notificationViewModel.markAsRead(notificationId)
            },
            onMarkAllAsRead = {
                notificationViewModel.markAllAsRead()
            },
            onNotificationClick = { notification ->
                if (!notification.isRead) {
                    notificationViewModel.markAsRead(notification.id)
                }
                val refType = notification.referenceType?.uppercase()
                val notifType = notification.type?.uppercase()
                when {
                    refType == "FEEDBACK_INVITATION" || notifType == "FEEDBACK_INVITATION" -> {
                        navController.navigate(Routes.PASSENGER_FEEDBACK) {
                            launchSingleTop = true
                        }
                    }
                    refType == "BOOKING" -> {
                        val refId = notification.referenceId?.takeIf { it.isNotBlank() }
                        if (refId != null) {
                            navController.navigate(Routes.passengerBookingDetail(refId))
                        } else {
                            navController.navigate(Routes.PASSENGER_BOOKINGS)
                        }
                    }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }

    // ----- ACTIVITY INBOX -----
    composable(Routes.PASSENGER_ACTIVITY_INBOX) {
        val context = LocalContext.current
        val source = remember(context) { ActivityInboxRepository(context) }
        val model: ActivityInboxViewModel = viewModel(
            factory = ActivityInboxViewModel.Factory(source))
        ActivityInboxScreen(model,
            onBooking = { navController.navigate(Routes.passengerBookingDetail(it)) },
            onBack = { navController.popBackStack() })
    }
}

fun NavGraphBuilder.passengerFeedbackGraph(
    navController: NavHostController,
    tokenManager: TokenManager,
    feedbackViewModel: FeedbackViewModel,
) {

    // ----- MY FEEDBACK (dùng ViewModel chung từ NavGraph) -----
    composable(Routes.PASSENGER_FEEDBACK) {
        MyFeedbackScreen(
            model = feedbackViewModel,
            onBack = {
                navController.popBackStack()
            }
        )
    }

    // ----- FEEDBACK THEO BOOKING -----
    composable(
        route = "passenger_feedback/{bookingId}",
        arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
    ) {
        val feedbackApi = remember(tokenManager) {
            RetrofitClient.createFeedbackApiService(tokenManager)
        }
        val source = remember(feedbackApi) {
            FeedbackRepository(feedbackApi)
        }
        val model: FeedbackViewModel = viewModel(
            factory = FeedbackViewModel.Factory(source)
        )

        MyFeedbackScreen(
            model = model,
            onBack = { navController.popBackStack() }
        )
    }
}