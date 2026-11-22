package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

enum class TripPhase { UPCOMING, ONGOING, FINISHED, CANCELLED, UNKNOWN }

fun tripPhase(status: String?): TripPhase =
    when (status?.uppercase()) {
        "IN_PROGRESS", "ONGOING", "RUNNING", "DEPARTED", "SAILING" -> TripPhase.ONGOING
        "SCHEDULED", "UPCOMING", "CONFIRMED", "PLANNED", "NOT_STARTED", "OPEN", "READY" -> TripPhase.UPCOMING
        "COMPLETED", "FINISHED", "ENDED", "ARRIVED" -> TripPhase.FINISHED
        "CANCELLED" -> TripPhase.CANCELLED
        else -> TripPhase.UNKNOWN
    }

fun tripStatusLabel(status: String?): String =
    when (tripPhase(status)) {
        TripPhase.ONGOING -> "Đang khởi hành"
        TripPhase.UPCOMING -> "Sắp khởi hành"
        TripPhase.FINISHED -> "Đã hoàn thành"
        TripPhase.CANCELLED -> "Đã hủy"
        TripPhase.UNKNOWN -> status?.takeIf { it.isNotBlank() } ?: "Đã đặt"
    }

/** Nhãn vàng nổi bật. */
@Composable
fun GoldBadge(text: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(50), color = OceanGold) {
        Text(
            text,
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
    }
}

/** Chấm "trực tiếp" nhấp nháy. */
@Composable
fun LiveDot(modifier: Modifier = Modifier, color: Color = OceanGold) {
    val transition = rememberInfiniteTransition(label = "live")
    val pulseScale by transition.animateFloat(
        1f, 2f, infiniteRepeatable(tween(1100), RepeatMode.Restart), label = "liveScale",
    )
    val pulseAlpha by transition.animateFloat(
        .55f, 0f, infiniteRepeatable(tween(1100), RepeatMode.Restart), label = "liveAlpha",
    )
    Box(modifier.size(14.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(8.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = pulseAlpha
                }
                .clip(CircleShape)
                .background(color)
        )
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
    }
}

/** Ô số liệu: [highlight] = nền vàng đậm, ngược lại nền vàng nhạt. */
@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier, highlight: Boolean = false) {
    Surface(
        modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (highlight) OceanGold else OceanGoldSoft,
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
                maxLines = 1,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) OceanNavy.copy(alpha = .75f) else OceanGoldDeep,
            )
        }
    }
}

/** Thanh thông báo nền vàng nhạt, viền vàng. */
@Composable
fun TripBanner(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = OceanGoldSoft,
        border = BorderStroke(1.dp, OceanGold),
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(OceanGold))
            Text(text, style = MaterialTheme.typography.bodySmall, color = OceanNavy)
        }
    }
}