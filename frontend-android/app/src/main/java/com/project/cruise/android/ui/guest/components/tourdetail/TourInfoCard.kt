package com.project.cruise.android.ui.guest.components.tourdetail

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
import com.project.cruise.android.data.dto.tour.PortInfoResponse
import com.project.cruise.android.ui.common.PassengerCard
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.passenger.components.mybookingdetail.GoldBadge
import com.project.cruise.android.ui.theme.*

/** Tab "Thông tin": du thuyền và hai đầu hành trình (cảng check-in → cảng kết thúc). */
@Composable
fun TourInfoCard(
    cruiseName: String?,
    cruiseCapacity: Number?,
    departurePort: PortInfoResponse? = null,
    arrivalPort: PortInfoResponse? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        PassengerCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier.size(46.dp).clip(CircleShape).background(OceanNavy),
                    contentAlignment = Alignment.Center,
                ) { PassengerIcon(PassengerGlyph.EXPLORE, OceanGold, Modifier.size(24.dp)) }
                Column(Modifier.weight(1f)) {
                    Text("Du thuyền", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                    Text(
                        cruiseName?.takeIf { it.isNotBlank() } ?: "Đang cập nhật",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                }
                cruiseCapacity?.let { GoldBadge("$it khách") }
            }
        }

        if (departurePort != null || arrivalPort != null) {
            PassengerCard {
                Text(
                    "Hành trình đi qua",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
                departurePort?.let {
                    PortRow(
                        label = "Điểm tập trung check-in",
                        name = "${it.name} (${it.city})",
                        address = it.address,
                        marker = OceanGold,
                        isLast = arrivalPort == null,
                    )
                }
                arrivalPort?.let {
                    PortRow(
                        label = "Cảng kết thúc hành trình",
                        name = "${it.name} (${it.city})",
                        address = null,
                        marker = OceanNavy,
                        isLast = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun PortRow(label: String, name: String, address: String?, marker: Color, isLast: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(marker))
            if (!isLast) Box(Modifier.width(2.dp).weight(1f).background(OceanLine))
        }
        Column(
            Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OceanGoldDeep, fontWeight = FontWeight.Bold)
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OceanNavy)
            address?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            }
        }
    }
}