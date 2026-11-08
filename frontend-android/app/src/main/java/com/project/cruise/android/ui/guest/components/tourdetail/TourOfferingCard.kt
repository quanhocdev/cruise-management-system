package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

/** Card dùng chung cho hoạt động trên tàu, dịch vụ và sản phẩm. */
@Composable
fun TourOfferingCard(
    name: String?,
    description: String?,
    price: BigDecimal?,
    imageUrl: String?,
    detail: String? = null,
) {
    PassengerCard {
        if (!imageUrl.isNullOrBlank()) CruiseVisual(imageUrl)
        Text(
            name ?: "Đang cập nhật",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = OceanSlate)
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (detail != null) PassengerPill(detail) else Spacer(Modifier.width(1.dp))
            Text(
                passengerMoney(price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanTeal,
            )
        }
    }
}