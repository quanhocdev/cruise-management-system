package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

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