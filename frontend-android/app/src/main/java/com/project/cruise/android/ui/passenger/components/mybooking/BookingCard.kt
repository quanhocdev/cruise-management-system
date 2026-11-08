package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingCard(booking: BookingResponse, onClick: () -> Unit) {
    PassengerCard(Modifier.clickable(onClick = onClick)) {
        // Header: icon + mã booking + trạng thái
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(color = OceanMintSoft, shape = RoundedCornerShape(14.dp)) {
                PassengerIcon(PassengerGlyph.TICKET, modifier = Modifier.padding(12.dp).size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("MÃ BOOKING", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    booking.bookingCode ?: "#${booking.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    maxLines = 1,
                )
            }
            PassengerPill(bookingLabel(booking.status), booking.status == BookingStatus.CONFIRMED)
        }

        HorizontalDivider(color = OceanLine)

        // Thông tin chính
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1.3f)) {
                PassengerInfo("Người liên hệ", booking.primaryContactName)
            }
            Column(Modifier.weight(1f)) {
                PassengerInfo(
                    "Hành khách",
                    booking.numberPassengers?.let { "$it khách" }
                        ?: booking.bookingPassengers?.size?.let { "$it khách" },
                )
            }
        }
        PassengerInfo("Ngày đặt", passengerDate(booking.createdAt))

        // Footer: tổng tiền + mũi tên
        Surface(color = OceanMintSoft.copy(alpha = .5f), shape = RoundedCornerShape(16.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Tổng giá trị booking",
                        style = MaterialTheme.typography.labelSmall,
                        color = OceanSlate,
                    )
                    Text(
                        passengerMoney(booking.totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OceanTeal,
                    )
                }
                PassengerIcon(PassengerGlyph.ARROW)
            }
        }
    }
}