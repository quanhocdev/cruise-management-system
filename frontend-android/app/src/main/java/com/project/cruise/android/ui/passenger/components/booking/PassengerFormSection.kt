package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerDraft

private val Genders = listOf("MALE" to "Nam", "FEMALE" to "Nữ", "OTHER" to "Khác")

private val IdTypes =
    listOf(
        "CCCD" to "CCCD",
        "PASSPORT" to "Hộ chiếu",
        "BIRTH_CERTIFICATE" to "Khai sinh",
        "OTHER" to "Khác",
    )

@Composable
fun PassengerFormSection(
    index: Int,
    passenger: PassengerDraft,
    enabled: Boolean,
    canRemove: Boolean,
    onChange: (PassengerDraft) -> Unit,
    onRemove: () -> Unit,
) {
    BookingSection(
        step = "03",
        title = "Hành khách ${index + 1}",
        trailing = {
            if (canRemove)
                TextButton(onClick = onRemove, enabled = enabled) {
                    Text("Xóa", color = OceanCoral, fontWeight = FontWeight.SemiBold)
                }
        },
    ) {
        BookingTextField("Họ và tên *", passenger.fullName, enabled) {
            onChange(passenger.copy(fullName = it))
        }
        DateOfBirthField(passenger.dateOfBirth, enabled) {
            onChange(passenger.copy(dateOfBirth = it))
        }
        ChoiceChips("Giới tính *", Genders, passenger.gender, enabled) {
            onChange(passenger.copy(gender = it))
        }
        ChoiceChips("Loại giấy tờ *", IdTypes, passenger.idCardType, enabled) {
            onChange(passenger.copy(idCardType = it))
        }
        BookingTextField("Số giấy tờ *", passenger.identificationNumber, enabled) {
            onChange(passenger.copy(identificationNumber = it))
        }
        BookingTextField("Số điện thoại", passenger.phone, enabled, KeyboardType.Phone) {
            onChange(passenger.copy(phone = it))
        }
        BookingTextField("Email", passenger.email, enabled, KeyboardType.Email) {
            onChange(passenger.copy(email = it))
        }
        BookingTextField("Ghi chú giấy tờ", passenger.documentNote, enabled) {
            onChange(passenger.copy(documentNote = it))
        }
    }
}

@Composable
fun AddPassengerButton(enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = OceanTeal, containerColor = Color.White),
    ) {
        Text("+  Thêm hành khách", fontWeight = FontWeight.SemiBold)
    }
}