package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

/** Model hiển thị cho lịch trình một ngày (tách khỏi DTO để component độc lập với data layer). */
data class ScheduleDayUi(
    val badge: String,
    val name: String,
    val description: String?,
    val stops: List<ScheduleStopUi>,
)

data class ScheduleStopUi(
    val order: String,
    val location: String,
    val timeText: String?,
    val visitName: String?,
    val visitDescription: String?,
    val visitPrice: String?,
)

@Composable
fun ScheduleDayCard(day: ScheduleDayUi) {
    PassengerCard {
        PassengerPill(day.badge)
        Text(
            day.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        day.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = OceanSlate)
        }
        if (day.stops.isNotEmpty()) HorizontalDivider(color = OceanLine)
        day.stops.forEachIndexed { index, stop ->
            StopRow(stop, isLast = index == day.stops.lastIndex)
        }
    }
}

@Composable
private fun StopRow(stop: ScheduleStopUi, isLast: Boolean) {
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(OceanNavy),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stop.order.ifBlank { "•" },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            if (!isLast)
                Box(Modifier.width(2.dp).weight(1f).background(OceanLine))
        }
        Column(
            Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                stop.location.ifBlank { "Điểm dừng" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            stop.timeText?.let {
                Text(it, color = OceanSlate, style = MaterialTheme.typography.bodySmall)
            }
            stop.visitName?.let { visitName ->
                Surface(
                    color = OceanMintSoft.copy(alpha = .5f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(visitName, fontWeight = FontWeight.SemiBold, color = OceanTeal)
                        stop.visitDescription?.takeIf { it.isNotBlank() }?.let {
                            Text(it, color = OceanSlate, style = MaterialTheme.typography.bodySmall)
                        }
                        stop.visitPrice?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanNavy,
                            )
                        }
                    }
                }
            }
        }
    }
}