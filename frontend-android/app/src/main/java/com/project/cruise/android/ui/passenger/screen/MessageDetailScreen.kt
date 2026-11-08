package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.MessageResponse
import com.project.cruise.android.ui.passenger.components.messagedetail.*
import com.project.cruise.android.ui.theme.*

@Composable
fun MessageDetailScreen(
    conversationId: String,
    messages: List<MessageResponse>,
    currentUserId: String,
    isLoading: Boolean,
    error: String?,
    onLoadMessages: () -> Unit,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    isSendingMessage: Boolean
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) { onLoadMessages() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    fun send() {
        val content = messageText.trim()
        if (content.isNotEmpty()) {
            onSendMessage(content)
            messageText = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MessageDetailHeader(
            conversationId = conversationId,
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                isLoading && messages.isEmpty() ->
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OceanTeal
                    )

                messages.isEmpty() && error == null ->
                    MessageEmptyState(modifier = Modifier.align(Alignment.Center))

                else ->
                    MessageList(
                        messages = messages,
                        currentUserId = currentUserId,
                        listState = listState
                    )
            }
        }

        error?.let { MessageErrorBanner(it) }

        MessageInputBar(
            value = messageText,
            onValueChange = { messageText = it },
            onSend = ::send,
            canSend = messageText.isNotBlank() && !isLoading && !isSendingMessage,
            isSending = isSendingMessage
        )
    }
}