package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.cruise.android.ui.passenger.components.messagedetail.MessageErrorBanner
import com.project.cruise.android.ui.passenger.components.passengermessages.*
import com.project.cruise.android.viewmodel.chat.MessagingViewModel

@Composable
fun PassengerMessagesScreen(
    viewModel: MessagingViewModel,
    onConversationClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        viewModel.loadBookings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConversationsHeader(
            onBackClick = onBackClick,
            onCreateClick = { showCreateDialog = true }
        )

        state.error?.let { MessageErrorBanner(it) }

        if (state.conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ConversationEmptyState(
                    onStartClick = { showCreateDialog = true },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            ConversationList(
                conversations = state.conversations,
                onConversationClick = onConversationClick,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showCreateDialog) {
        CreateConversationDialog(
            bookings = state.bookings,
            onDismiss = { showCreateDialog = false },
            onCreate = { bookingId, department ->
                viewModel.createOrGetConversation(
                    bookingId = bookingId,
                    department = department
                )
                showCreateDialog = false
            }
        )
    }
}