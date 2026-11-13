package com.project.cruise.android.ui.passenger.components.mybookingdetail

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
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingDetailHero(
    bookingCode: String,
    status: BookingStatus?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "MÃ BOOKING",
                style = MaterialTheme.typography.labelMedium,
                color = OceanMint,
            )
            Text(
                bookingCode,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                bookingLabel(status),
                modifier =
                    Modifier.clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = .16f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                statusMessage(status),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = .85f),
            )
        }
    }
}

private fun statusMessage(status: BookingStatus?): String =
    when (status) {
        BookingStatus.PENDING_PAYMENT -> "Đặt chỗ đang chờ thanh toán và xác nhận."
        BookingStatus.CONFIRMED -> "Đặt chỗ của bạn đã được xác nhận."
        BookingStatus.CANCELLED -> "Đặt chỗ này đã được hủy."
        null -> "Trạng thái đặt chỗ đang được cập nhật."
    }