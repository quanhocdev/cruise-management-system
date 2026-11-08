package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

private val Steps = listOf("Liên hệ", "Phòng", "Hành khách")

@Composable
fun BookingStepIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Steps.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(OceanNavy),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OceanNavy,
                )
            }
            if (index < Steps.lastIndex)
                HorizontalDivider(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    color = OceanLine,
                )
        }
    }
}