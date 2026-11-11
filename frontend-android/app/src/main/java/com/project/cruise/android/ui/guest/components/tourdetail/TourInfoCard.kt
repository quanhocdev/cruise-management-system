package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.PortInfoResponse
import com.project.cruise.android.ui.common.PassengerCard
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun TourInfoCard(
    cruiseName: String?,
    startDate: String,
    endDate: String,
    cruiseCapacity: Number?,
    departurePort: PortInfoResponse? = null,
    arrivalPort: PortInfoResponse? = null,
) {
    PassengerCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PassengerIcon(PassengerGlyph.EXPLORE, OceanTeal, Modifier.size(24.dp))
            Column(Modifier.weight(1f)) {
                Text("Du thuyền", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    cruiseName?.takeIf { it.isNotBlank() } ?: "Đang cập nhật",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
            }
        }
        HorizontalDivider(color = OceanLine)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DateTile("Khởi hành", startDate, Modifier.weight(1f))
            DateTile("Kết thúc", endDate, Modifier.weight(1f))
        }

        // --- Hiển thị thông tin Cảng check-in & Cảng kết thúc ---
        if (departurePort != null || arrivalPort != null) {
            HorizontalDivider(color = OceanLine)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                departurePort?.let { port ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PassengerIcon(PassengerGlyph.EXPLORE, OceanTeal, Modifier.size(20.dp))
                        Column {
                            Text("Điểm tập trung check-in", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                            Text(
                                "${port.name} (${port.city})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanNavy,
                            )
                            port.address?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                            }
                        }
                    }
                }

                arrivalPort?.let { port ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PassengerIcon(PassengerGlyph.ARROW, OceanTeal, Modifier.size(20.dp))
                        Column {
                            Text("Cảng kết thúc hành trình", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                            Text(
                                "${port.name} (${port.city})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OceanNavy,
                            )
                        }
                    }
                }
            }
        }

        cruiseCapacity?.let {
            Text(
                "Sức chứa du thuyền · $it khách",
                color = OceanSlate,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun DateTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = OceanMintSoft.copy(alpha = .5f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PassengerIcon(PassengerGlyph.CALENDAR, OceanTeal, Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = OceanSlate)
            }
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanNavy,
            )
        }
    }
}