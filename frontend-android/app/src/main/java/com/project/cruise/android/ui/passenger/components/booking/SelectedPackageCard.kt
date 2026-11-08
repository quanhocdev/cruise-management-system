package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

@Composable
fun SelectedPackageCard(
    name: String?,
    description: String?,
    price: BigDecimal?,
    capacityPerRoom: Int?,
) {
    PassengerCard {
        Text("GÓI ĐÃ CHỌN", style = MaterialTheme.typography.labelSmall, color = OceanTeal)
        Text(
            name ?: "Gói tour",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        description?.takeIf { it.isNotBlank() }?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(color = OceanLine)
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OceanMintSoft.copy(alpha = .5f))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Giá mỗi phòng", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    passengerMoney(price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
            }
            Text(
                capacityPerRoom?.let { "Tối đa $it khách/phòng" } ?: "Chưa cấu hình sức chứa",
                style = MaterialTheme.typography.labelMedium,
                color = OceanSlate,
            )
        }
    }
}