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
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.booking.BookingRepository
import com.project.cruise.android.data.repository.chat.ChatRepository
import com.project.cruise.android.data.repository.tour.TourRepository
import com.project.cruise.android.ui.passenger.screen.ProfileScreen
import com.project.cruise.android.ui.guest.screen.TourDetailPublicScreen
import com.project.cruise.android.ui.guest.screen.TourPublicScreen
import com.project.cruise.android.ui.passenger.screen.Dashboard
import com.project.cruise.android.ui.passenger.screen.MyBookingDetailScreen
import com.project.cruise.android.ui.passenger.screen.MyBookingsScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.passenger.BookingViewModel
import com.project.cruise.android.viewmodel.passenger.BookingViewModelFactory
import com.project.cruise.android.viewmodel.tour.TourViewModel
import com.project.cruise.android.viewmodel.tour.TourViewModelFactory
import com.project.cruise.android.viewmodel.chat.ChatViewModel
import com.project.cruise.android.viewmodel.chat.ChatViewModelFactory
import com.project.cruise.android.ui.passenger.screen.PassengerMessagesScreen
import com.project.cruise.android.ui.passenger.screen.MessageDetailScreen
import com.project.cruise.android.viewmodel.chat.MessagingViewModel
import com.project.cruise.android.viewmodel.chat.MessagingViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import com.project.cruise.android.data.repository.booking.ActivityRegistrationRepository
import com.project.cruise.android.viewmodel.auth.MeState
import com.project.cruise.android.ui.passenger.screen.BookingScreen
import com.project.cruise.android.data.repository.booking.PassengerBookingRepository
import com.project.cruise.android.data.repository.feedback.FeedbackRepository
import com.project.cruise.android.data.repository.finance.CheckoutRepository
import com.project.cruise.android.data.repository.notification.ActivityInboxRepository
import com.project.cruise.android.data.repository.notification.NotificationRepository
import com.project.cruise.android.data.repository.passenger.PassengerTripDetailRepository
import com.project.cruise.android.ui.screens.passenger.NotificationScreen
import com.project.cruise.android.viewmodel.passenger.NotificationViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookedToursViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModel
import com.project.cruise.android.viewmodel.passenger.PassengerBookingViewModelFactory
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailViewModel
import com.project.cruise.android.ui.screens.passenger.MyFeedbackScreen
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

fun NavGraphBuilder.passengerGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tokenManager: TokenManager,
    chatRepository: ChatRepository,
    feedbackViewModel: FeedbackViewModel,
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

    // ----- MY FEEDBACK -----
    composable(Routes.PASSENGER_FEEDBACK) {
        MyFeedbackScreen(
            model = feedbackViewModel,
            onBack = {
                navController.popBackStack()
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
    composable(Routes.PASSENGER_TOURS) { backStackEntry ->
        val context = androidx.compose.ui.platform.LocalContext.current
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

        val initialCriteria = com.project.cruise.android.ui.guest.components.tourcatalog.TourSearchCriteria(
            departure = backStackEntry.arguments?.getString("departure").orEmpty(),
            destination = backStackEntry.arguments?.getString("destination").orEmpty(),
            date = backStackEntry.arguments?.getString("date").orEmpty(),
        )

        com.project.cruise.android.ui.guest.screen.TourListScreen(
            state = listState,
            initialCriteria = initialCriteria,
            onTourClick = { tourId ->
                navController.navigate(Routes.passengerTourDetail(tourId))
            },
            onRetry = { tourViewModel.fetchPublicTours() },
            onBack = { navController.popBackStack() },
        )
    }

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

        val passengerBookingRepo = remember(tokenManager) {
            PassengerBookingRepository(
                tourApi = RetrofitClient.createTourApiService(tokenManager),
                bookingApi = RetrofitClient.createBookingApiService(tokenManager)
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
    }

    composable(Routes.PASSENGER_ACTIVITIES, arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context = androidx.compose.ui.platform.LocalContext.current
        val source = remember(context) { ActivityRegistrationRepository(context) }
        val model: com.project.cruise.android.viewmodel.passenger.ActivityRegistrationViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.ActivityRegistrationViewModel.Factory(source))
        com.project.cruise.android.ui.screens.passenger.ActivityRegistrationScreen(bookingId, model,
            onBack = { navController.popBackStack() },
            notificationButton = { ActivityInboxEntry { navController.navigate(Routes.PASSENGER_ACTIVITY_INBOX) } })
    }

    composable(Routes.PASSENGER_ACTIVITY_INBOX) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val source = remember(context) { ActivityInboxRepository(context) }
        val model: com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel.Factory(source))
        com.project.cruise.android.ui.screens.passenger.ActivityInboxScreen(model,
            onBooking = { navController.navigate(Routes.passengerBookingDetail(it)) }, onBack = { navController.popBackStack() })
    }

    composable(
        route = "passenger_feedback/{bookingId}",
        arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
    ) { entry ->
        // Khởi tạo FeedbackApiService thông qua RetrofitClient với tokenManager
        val feedbackApi = remember(tokenManager) {
            RetrofitClient.createFeedbackApiService(tokenManager)
        }

        // Khởi tạo FeedbackRepository với chỉ api
        val source = remember(feedbackApi) {
            FeedbackRepository(feedbackApi)
        }

        // Khởi tạo FeedbackViewModel qua Factory
        val model: com.project.cruise.android.viewmodel.passenger.FeedbackViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.FeedbackViewModel.Factory(source)
        )

        com.project.cruise.android.ui.screens.passenger.MyFeedbackScreen(
            model = model,
            onBack = { navController.popBackStack() }
        )
    }

    composable("passenger_bill/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val context = androidx.compose.ui.platform.LocalContext.current
        val source = remember(context) { CheckoutRepository(context) }
        val model: com.project.cruise.android.viewmodel.pos.CheckoutViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.pos.CheckoutViewModel.Factory(source, false))
        com.project.cruise.android.ui.screens.pos.CheckoutScreen(model, false, bookingId) { navController.popBackStack() }
    }

    composable("passenger_spending/{bookingId}", arguments = listOf(navArgument("bookingId") { type = NavType.LongType })) { entry ->
        val bookingId = entry.arguments?.getLong("bookingId") ?: return@composable
        val spendingApi = remember(tokenManager) { RetrofitClient.createBookingApiService(tokenManager) }
        val spending: com.project.cruise.android.viewmodel.passenger.SpendingViewModel = viewModel(
            factory = com.project.cruise.android.viewmodel.passenger.SpendingViewModel.Factory(spendingApi, bookingId))
        com.project.cruise.android.ui.screens.passenger.SpendingScreen(spending) { navController.popBackStack() }
    }
}

@androidx.compose.runtime.Composable
private fun ActivityInboxEntry(onClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val source = remember(context) { ActivityInboxRepository(context) }
    com.project.cruise.android.ui.screens.passenger.ActivityInboxButton(source, onClick)
}