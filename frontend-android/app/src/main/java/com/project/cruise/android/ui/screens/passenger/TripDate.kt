package com.project.cruise.android.ui.screens.passenger

import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun tripDate(value: String?): String =
    try {
        LocalDate.parse(value).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (_: Exception) { "Đang cập nhật" }
