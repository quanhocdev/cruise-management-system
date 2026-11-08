package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

private const val MIN_ROOMS = 1
private const val MAX_ROOMS = 20

@Composable
fun RoomCounter(
    rooms: Int,
    enabled: Boolean,
    capacityPerRoom: Int?,
    totalCapacity: Long,
    onChange: (Int) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FilledTonalIconButton(
            onClick = { onChange((rooms - 1).coerceAtLeast(MIN_ROOMS)) },
            enabled = enabled && rooms > MIN_ROOMS,
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = OceanMintSoft, contentColor = OceanTeal),
        ) {
            Text("−", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Column(
            Modifier.widthIn(min = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "$rooms",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            Text("phòng", style = MaterialTheme.typography.labelMedium, color = OceanSlate)
        }
        FilledTonalIconButton(
            onClick = { onChange((rooms + 1).coerceAtMost(MAX_ROOMS)) },
            enabled = enabled && rooms < MAX_ROOMS,
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = OceanMintSoft, contentColor = OceanTeal),
        ) {
            Text("+", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
    Text(
        if (capacityPerRoom != null) "Tối đa $totalCapacity hành khách"
        else "Chưa xác định sức chứa. Vui lòng tải lại gói tour.",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = OceanSlate,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}