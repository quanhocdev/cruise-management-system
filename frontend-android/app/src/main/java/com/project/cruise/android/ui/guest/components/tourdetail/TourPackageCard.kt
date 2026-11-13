package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.PassengerCard
import com.project.cruise.android.ui.common.PassengerPill
import com.project.cruise.android.ui.common.passengerMoney
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

@Composable
fun TourPackageCard(
    name: String,
    description: String?,
    maxPassengers: Number?,
    price: BigDecimal?,
    benefits: List<String>,
    isLoggedIn: Boolean,
    canBook: Boolean,
    onSelect: () -> Unit,
) {
    PassengerCard {
        Text(
            name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = OceanSlate)
        }
        maxPassengers?.let { PassengerPill("Tối đa $it khách / phòng") }

        Surface(color = OceanMintSoft.copy(alpha = .5f), shape = RoundedCornerShape(16.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text("Giá mỗi phòng", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    passengerMoney(price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
            }
        }

        if (benefits.isNotEmpty()) {
            HorizontalDivider(color = OceanLine)
            Text("Quyền lợi đi kèm", fontWeight = FontWeight.SemiBold, color = OceanNavy)
            benefits.forEach { benefit ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✓", color = OceanTeal, fontWeight = FontWeight.Bold)
                    Text(
                        benefit,
                        color = OceanSlate,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        OceanPrimaryButton(
            if (isLoggedIn) "Chọn gói này" else "Đăng nhập để đặt tour",
            onSelect,
            enabled = canBook,
        )
        if (!canBook)
            Text(
                "Hành trình hiện chưa nhận đặt chỗ.",
                style = MaterialTheme.typography.bodySmall,
                color = OceanSlate,
            )
    }
}