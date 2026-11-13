package com.project.cruise.android.ui.chatbox.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Model hiển thị một tin nhắn (tách khỏi model của ViewModel). */
data class ChatMessageUi(
    val text: String,
    val isUser: Boolean,
    val suggestions: List<String> = emptyList(),
)

@Composable
fun ChatMessageList(
    messages: List<ChatMessageUi>,
    isLoading: Boolean,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Tự cuộn xuống cuối khi có tin mới hoặc bot đang gõ
    LaunchedEffect(messages.size, isLoading) {
        val lastIndex = messages.size + (if (isLoading) 1 else 0) - 1
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(messages) { message ->
            ChatMessageBubble(message = message, onSuggestionClick = onSuggestionClick)
        }
        if (isLoading) item { ChatTypingIndicator() }
    }
}