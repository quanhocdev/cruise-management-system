package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.passenger.tripdetail.BookedTourItemDto
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.passenger.components.mybookingdetail.LiveDot
import com.project.cruise.android.ui.passenger.components.mybookingdetail.TripPhase
import com.project.cruise.android.ui.passenger.components.mybookingdetail.tripPhase
import com.project.cruise.android.ui.passenger.components.mybookingdetail.tripStatusLabel
import com.project.cruise.android.ui.theme.*

/** Thẻ chuyến đi cho booking đã thanh toán: ảnh, tên tour, tuyến, ngày, trạng thái chuyến. */
@Composable
fun ConfirmedTripCard(
    booking: BookingResponse,
    trip: BookedTourItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val phase = tripPhase(trip.statusTrip)
    val dep = trip.departurePortName?.takeIf { it.isNotBlank() }
    val arr = trip.arrivalPortName?.takeIf { it.isNotBlank() }
    val dates = listOfNotNull(trip.resolvedStartDate, trip.endDate)
        .filter { it.isNotBlank() }
        .joinToString(" – ") { passengerDate(it) }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, OceanGold),
        shadowElevation = 4.dp,
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(160.dp)
                    .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
            ) {
                if (!trip.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = trip.imageUrl,
                        contentDescription = trip.tourName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0.35f to Color.Transparent,
                            1f to OceanNavy.copy(alpha = .92f),
                        )
                    )
                )
                Surface(
                    Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = RoundedCornerShape(50),
                    color = if (phase == TripPhase.ONGOING) OceanGold else Color.White.copy(alpha = .9f),
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (phase == TripPhase.ONGOING) LiveDot(color = OceanNavy)
                        Text(
                            tripStatusLabel(trip.statusTrip),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                    }
                }
                Column(
                    Modifier.align(Alignment.BottomStart).padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    trip.tourCode?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = OceanGold, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        trip.tourName ?: "Chuyến du thuyền",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                    )
                    trip.cruiseName?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = OceanMint)
                    }
                }
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (dep != null || arr != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Place, null, tint = OceanGoldDeep, modifier = Modifier.size(18.dp))
                        Text(
                            "${dep ?: "Cảng đi"} → ${arr ?: "Cảng đến"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OceanNavy,
                        )
                    }
                }
                if (dates.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.DateRange, null, tint = OceanSlate, modifier = Modifier.size(18.dp))
                        Text(dates, style = MaterialTheme.typography.bodyMedium, color = OceanSlate)
                    }
                }
                HorizontalDivider(color = OceanLine)
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Mã booking", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                        Text(
                            booking.bookingCode ?: "#${booking.id}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                    }
                    Surface(shape = RoundedCornerShape(50), color = OceanGold) {
                        Row(
                            Modifier.padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Giám sát hành trình",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = OceanNavy,
                            )
                            Icon(Icons.Filled.KeyboardArrowRight, null, tint = OceanNavy)
                        }
                    }
                }
            }
        }
    }
}