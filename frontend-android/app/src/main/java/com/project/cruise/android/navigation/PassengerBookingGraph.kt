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
import com.project.cruise.android.MainActivity
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.booking.ActivityRegistrationRepository
import com.project.cruise.android.data.repository.booking.BookingRepository
import com.project.cruise.android.data.repository.booking.PassengerBookingRepository
import com.project.cruise.android.data.repository.finance.CheckoutRepository
import com.project.cruise.android.data.repository.passenger.PassengerTripDetailRepository
import com.project.cruise.android.ui.passenger.screen.BookingScreen
import com.project.cruise.android.ui.passenger.screen.MyBookingDetailScreen
import com.project.cruise.android.ui.passenger.screen.MyBookingsScreen
import com.project.cruise.android.ui.screens.passenger.ActivityRegistrationScreen
import com.project.cruise.android.ui.screens.passenger.SpendingScreen
import com.project.cruise.android.ui.screens.pos.CheckoutScreen
import com.project.cruise.android.viewmodel.passenger.ActivityRegistrationViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModelFactory
import com.project.cruise.android.viewmodel.passenger.PassengerBookedToursViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModelFactory
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailViewModel
import com.project.cruise.android.viewmodel.passenger.SpendingViewModel
import com.project.cruise.android.viewmodel.pos.CheckoutViewModel

fun NavGraphBuilder.passengerBookingGraph(
    navController: NavHostController,
    tokenManager: TokenManager,
) {

    // ----- TẠO BOOKING -----
    composable(
        route = Routes.PASSENGER_CREATE_BOOKING,
        arguments = listOf(
            navArgument("tourId") { type = NavType.StringType },
            navArgument("packageId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val tourId = backStackEntry.arguments?.getString("tourId") ?: return@composable
        val packageId = backStackEntry.arguments?.getString("packageId") ?: return@composable

        val context = LocalContext.current

        val passengerBookingRepo = remember(tokenManager, context) {
            val bookingRepository = BookingRepository(
                apiService = RetrofitClient.createBookingApiService(tokenManager),
                appContext = context.applicationContext
            )
            PassengerBookingRepository(
                tourApi = RetrofitClient.createTourApiService(tokenManager),
                bookingRepository = bookingRepository
            )
        }

        val bookingViewModel: PassengerBookingViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = PassengerBookingViewModelFactory(
                passengerBookingRepo,
                tourId,
                packageId
            )
        )
        val bookingState by bookingViewModel.state.collectAsState()

        BookingScreen(
            state = bookingState,
            onDraftChange = bookingViewModel::edit,
            onPickPassengerImage = { passengerIndex ->
                (context as? MainActivity)?.launchPickIdCardImage { uri ->
                    val currentDraft = bookingViewModel.state.value.draft
                    val updatedPassengers = currentDraft.passengers.mapIndexed { idx, p ->
                        if (idx == passengerIndex) p.copy(idCardImageUri = uri) else p
                    }
                    bookingViewModel.edit(currentDraft.copy(passengers = updatedPassengers))
                }
            },
            onSubmit = bookingViewModel::submit,
            onRetry = bookingViewModel::refreshPackage,
            onBack = { navController.popBackStack() },
            onDone = {
                navController.navigate(Routes.PASSENGER_BOOKINGS) {
                    popUpTo(Routes.PASSENGER_CREATE_BOOKING) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    // ----- BOOKINGS LIST -----
    composable(Routes.PASSENGER_BOOKINGS) { backStackEntry ->
        val bookingRepo = remember(tokenManager) { BookingRepository(RetrofitClient.createBookingApiService(tokenManager)) }
        val bookingViewModel: BookingViewModel =
            viewModel(factory = BookingViewModelFactory(bookingRepo))

        // Lấy ảnh, tuyến, ngày, trạng thái chuyến để làm thẻ booking đã thanh toán.
        val tripRepo = remember(tokenManager) {
            PassengerTripDetailRepository(RetrofitClient.createPassengerTripDetailApiService(tokenManager))
        }
        val tripsViewModel: PassengerBookedToursViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = PassengerBookedToursViewModel.Factory(tripRepo)
        )

        MyBookingsScreen(
            viewModel = bookingViewModel,
            tripsViewModel = tripsViewModel,
            onBookingClick = { bookingId ->
                navController.navigate(Routes.passengerBookingDetail(bookingId))
            },
            onBack = { navController.popBackStack() },
            onBrowseTours = { navController.navigate(Routes.PASSENGER_TOURS) },
            onAccount = { navController.navigate(Routes.PASSENGER_PROFILE) },
            onNotificationClick = {
                navController.navigate(Routes.PASSENGER_NOTIFICATIONS) {
                    launchSingleTop = true
                }
            }
        )
    }

    // ----- BOOKING DETAIL (đã thanh toán: có đủ các tab giám sát) -----
    composable(
        route = Routes.PASSENGER_BOOKING_DETAIL,
        arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
    ) { backStackEntry ->
        val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: return@composable
        val bookingRepo = remember(tokenManager) { BookingRepository(RetrofitClient.createBookingApiService(tokenManager)) }
        val bookingViewModel: BookingViewModel =
            viewModel(factory = BookingViewModelFactory(bookingRepo))

        val tripRepo = remember(tokenManager) {
            PassengerTripDetailRepository(RetrofitClient.createPassengerTripDetailApiService(tokenManager))
        }
        val tripViewModel: PassengerTripDetailViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = PassengerTripDetailViewModel.Factory(tripRepo)
        )

        MyBookingDetailScreen(
            bookingId = bookingId,
            viewModel = bookingViewModel,
            tripViewModel = tripViewModel,
            onBack = { navController.popBackStack() },
            onSpending = { navController.navigate("passenger_spending/$bookingId") },
            onActivities = { navController.navigate(Routes.passengerActivities(bookingId)) },
            onBill = { navController.navigate("passenger_bill/$bookingId") },
            onFeedback = { navController.navigate("passenger_feedback/$bookingId") },
            notificationButton = { ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) } }
        )
    }

    // ----- ĐĂNG KÝ HOẠT ĐỘNG -----
    composable(Routes.PASSENGER_ACTIVITIES, arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context = LocalContext.current
        val source = remember(context) { ActivityRegistrationRepository(context) }
        val model: ActivityRegistrationViewModel = viewModel(
            factory = ActivityRegistrationViewModel.Factory(source))
        ActivityRegistrationScreen(bookingId, model,
            onBack = { navController.popBackStack() },
            notificationButton = { ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) } })
    }

    // ----- HÓA ĐƠN -----
    composable("passenger_bill/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context = LocalContext.current
        val source = remember(context) { CheckoutRepository(context) }
        val model: CheckoutViewModel = viewModel(
            factory = CheckoutViewModel.Factory(source, false))
        CheckoutScreen(model, false, bookingId) { navController.popBackStack() }
    }

    // ----- CHI TIÊU -----
    composable("passenger_spending/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val spendingApi = remember(tokenManager) { RetrofitClient.createBookingApiService(tokenManager) }
        val spending: SpendingViewModel = viewModel(
            factory = SpendingViewModel.Factory(spendingApi, bookingId))
        SpendingScreen(spending) { navController.popBackStack() }
    }
}