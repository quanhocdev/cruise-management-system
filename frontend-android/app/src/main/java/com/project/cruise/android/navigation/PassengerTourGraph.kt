package com.project.cruise.android.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.notification.NotificationRepository
import com.project.cruise.android.data.repository.tour.TourRepository
import com.project.cruise.android.ui.guest.components.tourcatalog.TourSearchCriteria
import com.project.cruise.android.ui.guest.screen.TourDetailPublicScreen
import com.project.cruise.android.ui.guest.screen.TourListScreen
import com.project.cruise.android.ui.guest.screen.TourPublicScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.chat.ChatViewModel
import com.project.cruise.android.viewmodel.chat.ChatViewModelFactory
import com.project.cruise.android.viewmodel.passenger.NotificationViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModelFactory

fun NavGraphBuilder.passengerTourGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tokenManager: TokenManager,
) {

    // ----- TOURS (PUBLIC - MÀN HÌNH CHÍNH) -----
    composable(Routes.PASSENGER_TOURS) { backStackEntry ->
        val tourRepo = remember(tokenManager) {
            TourRepository(RetrofitClient.createTourApiService(tokenManager))
        }

        val tourViewModel: TourViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = TourViewModelFactory(tourRepo)
        )

        val chatViewModel: ChatViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = ChatViewModelFactory(RetrofitClient.createChatApiService(tokenManager))
        )

        val notificationRepo = remember(tokenManager) {
            NotificationRepository(RetrofitClient.createNotificationApiService(tokenManager))
        }
        val notificationViewModel: NotificationViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = NotificationViewModel.Factory(
                repository = notificationRepo,
                tokenManager = tokenManager
            )
        )
        val notificationState by notificationViewModel.state.collectAsState()

        TourPublicScreen(
            viewModel = tourViewModel,
            authViewModel = authViewModel,
            chatViewModel = chatViewModel,
            unreadCount = notificationState.unreadCount,
            onTourClick = { tourId ->
                navController.navigate(Routes.passengerTourDetail(tourId))
            },
            onNavigateToTourList = { criteria ->
                navController.navigate(
                    Routes.passengerTourList(
                        departure = criteria.departure,
                        destination = criteria.destination,
                        date = criteria.date,
                    )
                )
            },
            onLoginClick = {
                navController.navigate(Routes.GUEST)
            },
            onUserClick = {
                navController.navigate(Routes.PASSENGER_PROFILE)
            },
            onMyBookingsClick = {
                navController.navigate(Routes.PASSENGER_BOOKINGS)
            },
            onMessagesClick = {
                navController.navigate(Routes.PASSENGER_MESSAGES)
            },
            onNotificationClick = {
                navController.navigate(Routes.PASSENGER_NOTIFICATIONS) {
                    launchSingleTop = true
                }
            },
            onHomeClick = {}
        )
    }

    // ----- DANH SÁCH TOUR & BỘ LỌC -----
    composable(
        route = Routes.PASSENGER_TOUR_LIST,
        arguments = listOf(
            navArgument("departure") { type = NavType.StringType; defaultValue = "" },
            navArgument("destination") { type = NavType.StringType; defaultValue = "" },
            navArgument("date") { type = NavType.StringType; defaultValue = "" },
        )
    ) { backStackEntry ->
        val toursEntry = remember(backStackEntry) {
            runCatching { navController.getBackStackEntry(Routes.PASSENGER_TOURS) }
                .getOrDefault(backStackEntry)
        }
        val tourRepo = remember(tokenManager) {
            TourRepository(RetrofitClient.createTourApiService(tokenManager))
        }
        val tourViewModel: TourViewModel = viewModel(
            viewModelStoreOwner = toursEntry,
            factory = TourViewModelFactory(tourRepo)
        )
        val listState by tourViewModel.tourListState.collectAsState()

        val initialCriteria = TourSearchCriteria(
            departure = backStackEntry.arguments?.getString("departure").orEmpty(),
            destination = backStackEntry.arguments?.getString("destination").orEmpty(),
            date = backStackEntry.arguments?.getString("date").orEmpty(),
        )

        TourListScreen(
            state = listState,
            initialCriteria = initialCriteria,
            onTourClick = { tourId ->
                navController.navigate(Routes.passengerTourDetail(tourId))
            },
            onRetry = { tourViewModel.fetchPublicTours() },
            onBack = { navController.popBackStack() },
        )
    }

    // ----- TOUR DETAIL (PUBLIC) -----
    composable(
        route = Routes.PASSENGER_TOUR_DETAIL,
        arguments = listOf(navArgument("tourId") { type = NavType.StringType })
    ) { backStackEntry ->
        val tourId = backStackEntry.arguments?.getString("tourId") ?: return@composable
        val tourRepo = remember(tokenManager) { TourRepository(RetrofitClient.createTourApiService(tokenManager)) }
        val tourViewModel: TourViewModel = viewModel(factory = TourViewModelFactory(tourRepo))

        val sessionState by authViewModel.sessionState.collectAsState()
        val isLoggedIn = sessionState is SessionState.Authenticated

        TourDetailPublicScreen(
            tourId = tourId,
            viewModel = tourViewModel,
            isLoggedIn = isLoggedIn,
            onBookClick = { packageId ->
                navController.navigate(Routes.passengerCreateBooking(tourId, packageId))
            },
            onLoginRequired = { navController.navigate(Routes.LOGIN) },
            onBack = { navController.popBackStack() }
        )
    }
}