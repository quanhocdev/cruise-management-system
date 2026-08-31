package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.BuildConfig

@Composable
fun PosDashboardScreen(
    onBackClick: () -> Unit,
    onQrClick: () -> Unit,
    onNfcClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) { Text("← Thoát POS") }
            Surface(
                color = Color(0xFFE1F4EE),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "● Thiết bị sẵn sàng",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = Color(0xFF176B5B),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("Blue Horizon POS", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Thiết bị ${BuildConfig.POS_TERMINAL_CODE}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 5.dp)
        )
        Text(
            "Chọn phương thức nhận diện hành khách",
            modifier = Modifier.padding(top = 28.dp, bottom = 14.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        PosActionCard(
            title = "Quét mã QR",
            description = "Quét mã trên vé hoặc booking của hành khách",
            symbol = "▦",
            onClick = onQrClick
        )
        Spacer(Modifier.height(14.dp))
        PosActionCard(
            title = "Quét thẻ NFC",
            description = "Chạm thẻ hoặc vòng đeo tay vào thiết bị",
            symbol = "NFC",
            onClick = onNfcClick
        )

        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF4DF), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Text(
                "Khi mất mạng, giao dịch sẽ được lưu trên thiết bị và tự đồng bộ khi có kết nối.",
                color = Color(0xFF76551C),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PosActionCard(
    title: String,
    description: String,
    symbol: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFDDF3F0), RoundedCornerShape(14.dp))
                    .padding(17.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(symbol, color = Color(0xFF126A70), fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
