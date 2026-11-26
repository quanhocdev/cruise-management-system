package com.project.cruise.android.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.cruise.android.data.repository.chat.ChatRepository
import com.project.cruise.android.ui.passenger.screen.MessageDetailScreen
import com.project.cruise.android.ui.passenger.screen.PassengerMessagesScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.MeState
import com.project.cruise.android.viewmodel.chat.MessagingViewModel
import com.project.cruise.android.viewmodel.chat.MessagingViewModelFactory

fun NavGraphBuilder.passengerMessagingGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    chatRepository: ChatRepository,
) {

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
}