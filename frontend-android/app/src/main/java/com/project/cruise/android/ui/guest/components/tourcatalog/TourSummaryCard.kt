package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon

@Composable
fun TourSummaryCard(tour: PublicTourSummaryResponse, onClick: () -> Unit) {
    PassengerCard(Modifier.clickable(onClick = onClick)) {
        CruiseVisual(tour.cruiseImageUrl)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PassengerPill(
                tourSaleLabel(tour.statusBooking),
                tour.statusBooking == TourBookingStatus.OPEN,
            )
            tour.code?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = OceanSlate)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                tour.name ?: "Hành trình ${tour.code.orEmpty()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                tour.cruiseName ?: "Du thuyền chưa cập nhật",
                color = OceanSlate,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        // --- Hiển thị điểm check-in khởi hành thu gọn ngay trên thẻ ---
        // --- Hiển thị điểm check-in khởi hành thu gọn ngay trên thẻ ---
        tour.departurePort?.let { port ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // Thay bằng Icon chuẩn của Material Design
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = OceanTeal,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Check-in: ${port.name} (${port.city})",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Surface(color = OceanMintSoft.copy(alpha = .5f), shape = RoundedCornerShape(14.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PassengerIcon(PassengerGlyph.CALENDAR, OceanTeal, Modifier.size(20.dp))
                Text(
                    "${passengerDate(tour.startDate)} – ${passengerDate(tour.endDate)}",
                    color = OceanNavy,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        HorizontalDivider(color = OceanLine)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Giá từ", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    passengerMoney(tour.startingPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
            }
            PassengerIcon(PassengerGlyph.ARROW)
        }
    }
}