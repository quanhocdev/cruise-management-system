package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingSummaryHeader(
    bookings: List<BookingResponse>,
    isLoaded: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "ĐẶT CHỖ CỦA BẠN",
                    style = MaterialTheme.typography.labelMedium,
                    color = OceanMint,
                )
                Text(
                    "Hành trình của bạn",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            if (isLoaded)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderStat("Tổng", bookings.size)
                    HeaderStat(
                        "Đã xác nhận",
                        bookings.count { it.status == BookingStatus.CONFIRMED },
                    )
                    HeaderStat(
                        "Chờ thanh toán",
                        bookings.count { it.status == BookingStatus.PENDING_PAYMENT },
                    )
                }
        }
    }
}

@Composable
private fun RowScope.HeaderStat(label: String, value: Int) {
    Column(
        Modifier.weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = .14f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            "$value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = .8f),
            maxLines = 1,
        )
    }
}