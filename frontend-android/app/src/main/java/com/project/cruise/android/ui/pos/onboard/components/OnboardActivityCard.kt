package com.project.cruise.android.ui.pos.onboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.ui.common.OptionalInfoText
import com.project.cruise.android.ui.common.PassengerPill
import com.project.cruise.android.ui.common.PillTone
import com.project.cruise.android.ui.common.SelectableCard
import com.project.cruise.android.ui.theme.OceanMintSoft
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanSlate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardActivityCard(
    activity: ActivityCruiseTourResponse,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val facts =
        listOfNotNull(
            activity.cruiseAreaName.fact("Khu vực"),
            activity.startTime.fact("Bắt đầu"),
            activity.endTime.fact("Kết thúc"),
            activity.maxPassengers.fact("Tối đa", suffix = " người"),
        )
    val status = activity.status?.toString()?.takeIf { it.isNotBlank() }

    SelectableCard(selected = selected, onClick = onClick) {
        Text(
            text = activity.activityCruiseName ?: "Không có tên hoạt động",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )

        OptionalInfoText(
            value = activity.activityCruiseDescription,
            style = MaterialTheme.typography.bodyMedium,
        )

        if (facts.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                facts.forEach { FactChip(it) }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Giá: OptionalInfoText không phát ra gì khi price rỗng
            Column(Modifier.weight(1f)) {
                OptionalInfoText(activity.price, prefix = "Giá: ", topSpace = 0.dp, emphasized = true)
            }
            status?.let { PassengerPill(it, PillTone.NEUTRAL) }
        }
    }
}

@Composable
private fun FactChip(text: String) {
    Surface(color = OceanMintSoft.copy(alpha = .6f), shape = RoundedCornerShape(10.dp)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = OceanSlate,
        )
    }
}

/** "Nhãn: giá trị" nếu giá trị không rỗng, ngược lại null. */
private fun Any?.fact(label: String, suffix: String = ""): String? =
    this?.toString()?.takeIf { it.isNotBlank() }?.let { "$label: $it$suffix" }