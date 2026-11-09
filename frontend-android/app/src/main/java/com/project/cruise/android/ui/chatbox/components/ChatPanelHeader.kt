package com.project.cruise.android.ui.chatbox.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.theme.*

@Composable
fun ChatPanelHeader(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(OceanNavy, OceanTeal)))
                .padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = .16f)),
            contentAlignment = Alignment.Center,
        ) {
            PassengerIcon(PassengerGlyph.MESSAGE, OceanMint, Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                "Trợ lý du lịch",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(OceanMint))
                Text(
                    "Luôn sẵn sàng hỗ trợ",
                    color = OceanMint,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = Color.White)
        }
    }
}