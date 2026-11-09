package com.project.cruise.android.ui.chatbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun ChatMessageBubble(
    message: ChatMessageUi,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            color = if (message.isUser) OceanTeal else OceanPearl,
            shape =
                RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (message.isUser) 18.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 18.dp,
                ),
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isUser) Color.White else OceanNavy,
            )
        }

        // Các nút gợi ý nằm dưới tin nhắn của bot
        if (!message.isUser && message.suggestions.isNotEmpty()) {
            ChatSuggestionChips(message.suggestions, onSuggestionClick)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatSuggestionChips(
    suggestions: List<String>,
    onClick: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        suggestions.forEach { suggestion ->
            Surface(
                onClick = { onClick(suggestion) },
                shape = RoundedCornerShape(50),
                color = Color.White,
                border = BorderStroke(1.dp, OceanTeal.copy(alpha = .6f)),
            ) {
                Text(
                    suggestion,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = OceanTeal,
                )
            }
        }
    }
}