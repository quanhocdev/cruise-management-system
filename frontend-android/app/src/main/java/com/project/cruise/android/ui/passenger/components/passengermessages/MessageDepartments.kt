package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Danh sách phòng ban: mã gửi lên server -> tên hiển thị. */
val MessageDepartments = listOf(
    "ONBOARD" to "Nhân viên trên tàu",
    "CONVENIENCE" to "Dịch vụ tiện ích",
    "SHORE" to "Hoạt động trên bờ"
)

fun departmentLabel(code: String): String =
    MessageDepartments.firstOrNull { it.first == code }?.second ?: code

fun departmentIcon(code: String): ImageVector =
    when (code) {
        "CONVENIENCE" -> Icons.Filled.Settings
        "SHORE" -> Icons.Filled.LocationOn
        else -> Icons.Filled.Person
    }