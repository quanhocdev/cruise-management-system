package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
fun ConversationsHeader(
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
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

        Column(Modifier.weight(1f)) {
            Text(
                text = "Tin nhắn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Trao đổi với bộ phận hỗ trợ",
                style = MaterialTheme.typography.bodySmall,
                color = OceanMint
            )
        }

        FilledIconButton(
            onClick = onCreateClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.White.copy(alpha = .16f),
                contentColor = OceanMint
            ),
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Tạo cuộc trò chuyện mới")
        }

        Spacer(Modifier.width(8.dp))
    }
}