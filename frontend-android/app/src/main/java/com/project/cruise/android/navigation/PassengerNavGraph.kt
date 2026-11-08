package com.project.cruise.android.navigation

import androidx.compose.material3.CircularProgressIndicator
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
import com.project.cruise.android.data.repository.ChatRepository
import com.project.cruise.android.data.repository.TourRepository
import com.project.cruise.android.ui.screens.passenger.ProfileScreen
import com.project.cruise.android.ui.screens.guest.TourDetailPublicScreen
import com.project.cruise.android.ui.screens.guest.TourPublicScreen
import com.project.cruise.android.ui.screens.passenger.Dashboard
import com.project.cruise.android.ui.screens.passenger.MyBookingDetailScreen
import com.project.cruise.android.ui.passenger.screen.MyBookingsScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModelFactory
import com.project.cruise.android.viewmodel.tour.TourViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModelFactory
import com.project.cruise.android.viewmodel.chat.ChatViewModel
import com.project.cruise.android.viewmodel.chat.ChatViewModelFactory
import com.project.cruise.android.ui.screens.passenger.PassengerMessagesScreen
import com.project.cruise.android.ui.screens.passenger.MessageDetailScreen
import com.project.cruise.android.viewmodel.chat.MessagingViewModel
import com.project.cruise.android.viewmodel.chat.MessagingViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect

import com.project.cruise.android.viewmodel.auth.MeState

import com.project.cruise.android.ui.screens.passenger.CreateBookingScreen
import com.project.cruise.android.data.repository.PassengerBookingRepository
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModelFactory

fun NavGraphBuilder.passengerGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    apiService: ApiService,
    chatRepository: ChatRepository
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
            onMyBookingsClick = {
                navController.navigate(Routes.PASSENGER_BOOKINGS)
            },
            onMessagesClick = {
                navController.navigate(Routes.PASSENGER_MESSAGES)
            },
            onLogout = {
                navController.navigate(Routes.GUEST) {
                    popUpTo(Routes.PASSENGER_TOURS) { inclusive = true }
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

    // ----- TOURS (PUBLIC - MÀN HÌNH CHÍNH) -----

    composable(Routes.PASSENGER_TOURS) {
        val tourRepo = remember(apiService) {
            TourRepository(apiService)
        }

        val tourViewModel: TourViewModel = viewModel(
            factory = TourViewModelFactory(tourRepo)
        )

        val chatViewModel: ChatViewModel = viewModel(
            factory = ChatViewModelFactory(apiService)
        )

        TourPublicScreen(
            viewModel = tourViewModel,
            authViewModel = authViewModel,
            chatViewModel = chatViewModel,
            onTourClick = { tourId ->
                navController.navigate(Routes.passengerTourDetail(tourId))
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
            onHomeClick = {}
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

        composable(
            route = Routes.PASSENGER_CREATE_BOOKING,
            arguments = listOf(
                navArgument("tourId") { type = NavType.StringType },
                navArgument("packageId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: return@composable
            val packageId = backStackEntry.arguments?.getString("packageId") ?: return@composable
            val bookingViewModel: PassengerBookingViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = PassengerBookingViewModelFactory(
                    PassengerBookingRepository(apiService),
                    tourId,
                    packageId
                )
            )
            val bookingState by bookingViewModel.state.collectAsState()

            CreateBookingScreen(
                state = bookingState,
                onDraftChange = bookingViewModel::edit,
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
    composable(Routes.PASSENGER_BOOKINGS) {
        val bookingRepo = remember(apiService) { BookingRepository(apiService) }
        val bookingViewModel: BookingViewModel =
            viewModel(factory = BookingViewModelFactory(bookingRepo))

        MyBookingsScreen(
            viewModel = bookingViewModel,
            onBookingClick = { bookingId ->
                navController.navigate(Routes.passengerBookingDetail(bookingId))
            },
            onBack = { navController.popBackStack() },
            onBrowseTours = { navController.navigate(Routes.PASSENGER_TOURS) },
            onAccount = { navController.navigate(Routes.PASSENGER_PROFILE) }
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
            onBack = { navController.popBackStack() },
            onSpending = { navController.navigate("passenger_spending/$bookingId") },
            onActivities = { navController.navigate(Routes.passengerActivities(bookingId)) },
            onBill = { navController.navigate("passenger_bill/$bookingId") },
            onFeedback = { navController.navigate("passenger_feedback/$bookingId") },
            notificationButton = { ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) } }
        )
    }
    // ----- PASSENGER MESSAGES -----
    composable(Routes.PASSENGER_MESSAGES) { backStackEntry ->

        val messagingViewModel: MessagingViewModel = viewModel(
            viewModelStoreOwner = backStackEntry,
            factory = MessagingViewModelFactory(chatRepository)
        )

        LaunchedEffect(Unit) {
            messagingViewModel.loadConversations()
            messagingViewModel.loadBookings()
            authViewModel.getCurrentUser()
        }

        PassengerMessagesScreen(
            viewModel = messagingViewModel,
            onConversationClick = { conversationId ->
                navController.navigate(
                    Routes.passengerMessageDetail(conversationId)
                )
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    // ----- PASSENGER MESSAGE DETAIL -----
    composable(
        route = Routes.PASSENGER_MESSAGE_DETAIL,
        arguments = listOf(
            navArgument("conversationId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val conversationId = backStackEntry.arguments
            ?.getString("conversationId")
            ?: return@composable

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Routes.PASSENGER_MESSAGES)
        }

        val messagingViewModel: MessagingViewModel = viewModel(
            viewModelStoreOwner = parentEntry,
            factory = MessagingViewModelFactory(chatRepository)
        )

        val state by messagingViewModel.uiState.collectAsState()
        val meState by authViewModel.meState.collectAsState()

        val currentUserId = (meState as? MeState.Success)
            ?.userId
            .orEmpty()

        if (currentUserId.isNotBlank()) {
            MessageDetailScreen(
                conversationId = conversationId,
                messages = state.messages,
                currentUserId = currentUserId,
                isLoading = state.isLoadingMessages,
                isSendingMessage = state.isSendingMessage,
                error = state.error,
                onLoadMessages = {},
                onSendMessage = messagingViewModel::sendMessage,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        } else {
            CircularProgressIndicator()

        }

            LaunchedEffect(conversationId) {
            val conversation = state.conversations
                .firstOrNull { it.id == conversationId }

            if (conversation != null) {
                messagingViewModel.openConversation(conversation)
                messagingViewModel.connectChat(conversationId)
            }
        }

        DisposableEffect(conversationId) {
            onDispose {
                messagingViewModel.disconnectChat()
            }
        }

        MessageDetailScreen(
            conversationId = conversationId,
            messages = state.messages,
            currentUserId = currentUserId,
            isLoading = state.isLoadingMessages,
            error = state.error,
            onLoadMessages = {},
            onSendMessage = { content ->
                messagingViewModel.sendMessage(content)
            },
            onBackClick = {
                navController.popBackStack()
            },
            isSendingMessage = state.isSendingMessage
        )
    }
    composable(Routes.PASSENGER_ACTIVITIES, arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context = androidx.compose.ui.platform.LocalContext.current
        val source = remember(context) { com.project.cruise.android.data.repository.ActivityRegistrationRepository(context) }
        val model: com.project.cruise.android.viewmodel.passenger.ActivityRegistrationViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.ActivityRegistrationViewModel.Factory(source))
        com.project.cruise.android.ui.screens.passenger.ActivityRegistrationScreen(bookingId, model,
            onBack={navController.popBackStack()},
            notificationButton={ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) }})
    }
    composable(Routes.PASSENGER_ACTIVITY_INBOX) {
        val context=androidx.compose.ui.platform.LocalContext.current
        val source=remember(context) { com.project.cruise.android.data.repository.ActivityInboxRepository(context) }
        val model:com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel=viewModel(
            factory=com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel.Factory(source))
        com.project.cruise.android.ui.screens.passenger.ActivityInboxScreen(model,
            onBooking={navController.navigate(Routes.passengerBookingDetail(it))},onBack={navController.popBackStack()})
    }
    composable("passenger_feedback/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context=androidx.compose.ui.platform.LocalContext.current
        val source = remember(apiService,context) { com.project.cruise.android.data.repository.FeedbackRepository(apiService,context) }
        val model: com.project.cruise.android.viewmodel.passenger.FeedbackViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.FeedbackViewModel.Factory(source))
        com.project.cruise.android.ui.screens.passenger.MyFeedbackScreen(bookingId, model) { navController.popBackStack() }
    }
    composable("passenger_bill/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId=entry.arguments?.getLong("bookingId")?:return@composable
        val context=androidx.compose.ui.platform.LocalContext.current
        val source=remember(context){com.project.cruise.android.data.repository.CheckoutRepository(context)}
        val model:com.project.cruise.android.viewmodel.pos.CheckoutViewModel=viewModel(
            factory=com.project.cruise.android.viewmodel.pos.CheckoutViewModel.Factory(source,false))
        com.project.cruise.android.ui.screens.pos.CheckoutScreen(model,false,bookingId){navController.popBackStack()}
    }
    composable("passenger_spending/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val spending: com.project.cruise.android.viewmodel.passenger.SpendingViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.SpendingViewModel.Factory(apiService, bookingId))
        com.project.cruise.android.ui.screens.passenger.SpendingScreen(spending) { navController.popBackStack() }
    }

}

@androidx.compose.runtime.Composable
private fun ActivityInboxEntry(onClick: ()->Unit) {
    val context=androidx.compose.ui.platform.LocalContext.current
    val source=remember(context) { com.project.cruise.android.data.repository.ActivityInboxRepository(context) }
    com.project.cruise.android.ui.screens.passenger.ActivityInboxButton(source,onClick)
}
