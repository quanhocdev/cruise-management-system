package com.project.cruise.android.ui.common

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun passengerMoney(amount: BigDecimal?): String =
    amount?.let { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it) }
        ?: "Chưa có giá"

fun passengerDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "Chưa cập nhật"
    val date =
        runCatching { OffsetDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDate.parse(raw) }.getOrNull()
    return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: raw
}

fun passengerTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "Chưa cập nhật"
    val time =
        runCatching { OffsetDateTime.parse(raw).toLocalTime() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw).toLocalTime() }.getOrNull()
            ?: runCatching { LocalTime.parse(raw) }.getOrNull()
    return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: raw
}