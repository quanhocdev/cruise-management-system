package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*

@Composable
fun PassengerInfoCard(passenger: BookingPassengerInfoResponse) {
    val name = passenger.fullName?.takeIf { it.isNotBlank() } ?: "Hành khách"
    val status = passenger.checkinStatus?.uppercase()

    PassengerCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(OceanNavy),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    name.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanMint,
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
                PassengerPill(checkinLabel(status, passenger.checkinStatus), status in CHECKED_IN)
            }
        }

        HorizontalDivider(color = OceanLine)

        DetailRow(Icons.Filled.Person, "Giới tính", genderLabel(passenger.gender))
        passenger.phoneNumber?.takeIf { it.isNotBlank() }?.let {
            DetailRow(Icons.Filled.Phone, "Số điện thoại", it)
        }
        passenger.email?.takeIf { it.isNotBlank() }?.let {
            DetailRow(Icons.Filled.Email, "Email", it)
        }
        passenger.checkedInAt?.let {
            DetailRow(Icons.Filled.DateRange, "Ngày check-in", passengerDate(it))
        }
    }
}

private val CHECKED_IN = listOf("CHECKED_IN", "CHECKEDIN")

private fun checkinLabel(status: String?, raw: String?): String =
    when (status) {
        "CHECKED_IN",
        "CHECKEDIN" -> "Đã check-in"
        "PENDING",
        "NOT_CHECKED_IN" -> "Chưa check-in"
        "CHECKED_OUT" -> "Đã check-out"
        null -> "Chưa có trạng thái check-in"
        else -> raw ?: ""
    }

private fun genderLabel(gender: String?): String? =
    when (gender?.uppercase()) {
        "MALE" -> "Nam"
        "FEMALE" -> "Nữ"
        "OTHER" -> "Khác"
        else -> gender
    }