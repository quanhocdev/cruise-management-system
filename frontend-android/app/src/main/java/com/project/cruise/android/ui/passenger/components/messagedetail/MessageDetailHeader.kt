package com.project.cruise.android.ui.passenger.components.messagedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun MessageDetailHeader(
    conversationId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(OceanNavy, OceanTeal)))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = .16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = OceanMint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = "Tin nhắn hỗ trợ",
                color = Color.White,
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
}