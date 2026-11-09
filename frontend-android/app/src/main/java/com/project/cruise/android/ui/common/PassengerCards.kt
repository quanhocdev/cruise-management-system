package com.project.cruise.android.ui.common

import androidx.compose.foundation.BorderStroke
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
import com.project.cruise.android.ui.theme.*

@Composable
fun PassengerCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine.copy(alpha = .55f)),
        shadowElevation = 2.dp,
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

/** Sắc thái màu của pill trạng thái. */
enum class PillTone {
    POSITIVE,
    NEUTRAL,
    WARNING,
    DANGER,
}

/** Phiên bản cũ: giữ nguyên chữ ký để các màn đang dùng không phải sửa. */
@Composable
fun PassengerPill(label: String, positive: Boolean = true) =
    PassengerPill(label, if (positive) PillTone.POSITIVE else PillTone.NEUTRAL)

/** Pill có chấm màu đầu dòng, đổi màu theo [tone]. */
@Composable
fun PassengerPill(label: String, tone: PillTone) {
    val (container, content) =
        when (tone) {
            PillTone.POSITIVE -> OceanMintSoft to OceanTeal
            PillTone.NEUTRAL -> OceanLavender to OceanNavy
            PillTone.WARNING -> Color(0xFFFFF1D6) to Color(0xFF9A5B00)
            PillTone.DANGER -> OceanCoral.copy(alpha = .14f) to OceanCoral
        }
    Surface(shape = RoundedCornerShape(100.dp), color = container) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(content))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = content,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun PassengerSection(title: String, subtitle: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.width(4.dp).height(if (subtitle != null) 36.dp else 24.dp).clip(RoundedCornerShape(2.dp)).background(OceanTeal))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            }
        }
    }
}

@Composable
fun PassengerInfo(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = OceanSlate)
        Text(
            value?.takeIf { it.isNotBlank() } ?: "Chưa cập nhật",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = OceanNavy,
        )
    }
}