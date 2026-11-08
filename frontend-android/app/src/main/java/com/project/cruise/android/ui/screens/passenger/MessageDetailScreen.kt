
package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.MessageResponse
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
    onBackClick: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        onLoadMessages()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OceanNavy)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("Quay lại", color = OceanMint)
            }

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    text = "Tin nhắn hỗ trợ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Cuộc trò chuyện #${conversationId.take(8)}",
                    color = OceanMint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            when {
                isLoading && messages.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                messages.isEmpty() && error == null -> {
                    Text(
                        text = "Chưa có tin nhắn. Hãy gửi lời chào đầu tiên!",
                        modifier = Modifier.align(Alignment.Center),
                        color = OceanSlate
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = messages,
                            key = { it.id }
                        ) { message ->
                            val isMine = message.senderUserId == currentUserId

                            MessageBubble(
                                message = message,
                                isMine = isMine
                            )
                        }
                    }
                }
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        val content = messageText.trim()
                        if (content.isNotEmpty()) {
                            onSendMessage(content)
                            messageText = ""
                        }
                    }
                )
            )

            Spacer(Modifier.width(8.dp))

            Button(
                enabled = messageText.isNotBlank() && !isLoading,
                onClick = {
                    val content = messageText.trim()
                    if (content.isNotEmpty()) {
                        onSendMessage(content)
                        messageText = ""
                    }
                },
                shape = RoundedCornerShape(50)
            ) {
                Text("Gửi")
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: MessageResponse,
    isMine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .background(
                    color = if (isMine) OceanNavy
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMine) 18.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 18.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isMine) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )

            message.sentAt?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = if (isMine) OceanMint
                    else OceanSlate,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
