package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun ConversationEmptyState(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(OceanMintSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = null,
                tint = OceanTeal,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = "Chưa có cuộc trò chuyện nào",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy
        )
        Text(
            text = "Bắt đầu trò chuyện để được nhân viên hỗ trợ ngay trong hành trình của bạn.",
            style = MaterialTheme.typography.bodyMedium,
            color = OceanSlate,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onStartClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OceanTeal,
                contentColor = Color.White
            )
        ) {
            Text("Bắt đầu trò chuyện", fontWeight = FontWeight.SemiBold)
        }
    }
}