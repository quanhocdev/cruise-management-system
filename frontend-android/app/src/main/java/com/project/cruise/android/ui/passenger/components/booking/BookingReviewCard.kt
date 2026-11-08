package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

@Composable
fun BookingReviewCard(
    roomCount: Int,
    passengerCount: Int,
    estimatedTotal: BigDecimal?,
) {
    PassengerCard {
        PassengerSection("Kiểm tra đặt chỗ")
        Text(
            "$roomCount phòng · $passengerCount hành khách",
            style = MaterialTheme.typography.bodyLarge,
            color = OceanSlate,
        )
        HorizontalDivider(color = OceanLine)
        Text(
            "Tạm tính theo số phòng",
            style = MaterialTheme.typography.labelMedium,
            color = OceanSlate,
        )
        Text(
            passengerMoney(estimatedTotal),
            style = MaterialTheme.typography.headlineMedium,
            color = OceanTeal,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Tổng giá trị chính thức được xác định khi tạo đặt chỗ.",
            style = MaterialTheme.typography.bodySmall,
            color = OceanSlate,
        )
    }
}