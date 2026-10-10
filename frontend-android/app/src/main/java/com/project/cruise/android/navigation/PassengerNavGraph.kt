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
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.repository.BookingRepository
import com.project.cruise.android.data.repository.TourRepository
import com.project.cruise.android.ui.screens.ProfileScreen
import com.project.cruise.android.ui.screens.TourDetailPublicScreen
import com.project.cruise.android.ui.screens.TourPublicScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard
import com.project.cruise.android.ui.screens.passenger.MyBookingDetailScreen
import com.project.cruise.android.ui.screens.passenger.MyBookingsScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModelFactory
import com.project.cruise.android.viewmodel.tour.TourViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModelFactory

fun NavGraphBuilder.passengerGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    apiService: ApiService
) {

    // ----- PROFILE -----
    composable(Routes.PASSENGER_PROFILE) {
        ProfileScreen(
            authViewModel = authViewModel,
            onHomeClick = {
                navController.navigate(Routes.PASSENGER_TOURS) {
                    popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                }
            },
            onMyBookingsClick = { navController.navigate(Routes.PASSENGER_BOOKINGS) },
            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                }
            },
            onNotificationClick = {
                // TODO: chuyển sang trang thông báo
            }
        )
    }

    // ----- DASHBOARD -----
    composable(Routes.PASSENGER_DASHBOARD) {
        Dashboard(
            viewModel = authViewModel,
            onBrowseTours = { navController.navigate(Routes.PASSENGER_TOURS) },
            onMyBookings = { navController.navigate(Routes.PASSENGER_BOOKINGS) },
            notificationButton = {},
            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_DASHBOARD) { inclusive = true }
                }
            }
        )
    }

    // ----- TOURS (PUBLIC - MÀN HÌNH CHÍNH) -----
    composable(Routes.PASSENGER_TOURS) {
        val tourRepo = remember(apiService) { TourRepository(apiService) }
        val tourViewModel: TourViewModel = viewModel(factory = TourViewModelFactory(tourRepo))

        TourPublicScreen(
            viewModel = tourViewModel,
            authViewModel = authViewModel,
            onTourClick = { tourId ->
                navController.navigate(Routes.passengerTourDetail(tourId))
            },
            onLoginClick = { navController.navigate(Routes.GUEST) },       // Chưa đăng nhập -> Guest
            onUserClick = { navController.navigate(Routes.PASSENGER_PROFILE) }, // Đã đăng nhập -> Profile
            onMyBookingsClick = { navController.navigate(Routes.PASSENGER_BOOKINGS) },
            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
                }
            },
            onHomeClick = {
                // Đang ở trang chủ rồi
            },
            onNotificationClick = {
                // TODO: chuyển sang màn hình thông báo
            }
        )
    }

    // ----- TOUR DETAIL (PUBLIC) -----
    composable(
        route = Routes.PASSENGER_TOUR_DETAIL,
        arguments = listOf(navArgument("tourId") { type = NavType.StringType })
    ) { backStackEntry ->
        val tourId = backStackEntry.arguments?.getString("tourId") ?: return@composable
        val tourRepo = remember(apiService) { TourRepository(apiService) }
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

    // ----- BOOKINGS LIST -----
    composable(Routes.PASSENGER_BOOKINGS) {
        val bookingRepo = remember(apiService) { BookingRepository(apiService) }
        val bookingViewModel: BookingViewModel =
            viewModel(factory = BookingViewModelFactory(bookingRepo))

        MyBookingsScreen(
            viewModel = bookingViewModel,
            onBookingClick = { bookingId ->
                navController.navigate(Routes.passengerBookingDetail(bookingId))
            },
            onBack = { navController.popBackStack() }
        )
    }

    // ----- BOOKING DETAIL -----
    composable(
        route = Routes.PASSENGER_BOOKING_DETAIL,
        arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: return@composable
        val bookingRepo = remember(apiService) { BookingRepository(apiService) }
        val bookingViewModel: BookingViewModel =
            viewModel(factory = BookingViewModelFactory(bookingRepo))

        MyBookingDetailScreen(
            bookingId = bookingId,
            viewModel = bookingViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}