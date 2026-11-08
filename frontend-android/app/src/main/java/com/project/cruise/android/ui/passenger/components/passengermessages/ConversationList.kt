package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.ConversationResponse

@Composable
fun ConversationList(
    conversations: List<ConversationResponse>,
    onConversationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = conversations, key = { it.id }) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClick = { onConversationClick(conversation.id) }
            )
        }
    }
}