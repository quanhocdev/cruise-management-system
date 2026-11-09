package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingSummaryCard(booking: BookingResponse) {
    PassengerCard {
        Text(
            "Tổng giá trị booking",
            color = OceanSlate,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            passengerMoney(booking.totalAmount),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OceanTeal,
        )
        HorizontalDivider(color = OceanLine)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoTile("Ngày đặt", passengerDate(booking.createdAt), Modifier.weight(1f))
            InfoTile(
                "Số hành khách",
                booking.numberPassengers?.let { "$it khách" }
                    ?: booking.bookingPassengers?.size?.let { "$it khách" },
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = OceanMintSoft.copy(alpha = .5f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OceanSlate)
            Text(
                value?.takeIf { it.isNotBlank() } ?: "—",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanNavy,
            )
        }
    }
}