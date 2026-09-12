//package com.project.cruise.android.ui.screens.passenger
//
//import java.math.BigDecimal
//import java.text.NumberFormat
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.Locale
//
//internal fun activityTime(value: String?): String = try {
//    LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("HH:mm · dd/MM/yyyy"))
//} catch (_: Exception) { "Đang cập nhật" }
//
//internal fun activityPrice(value: BigDecimal?): String = when {
//    value == null || value.signum() < 0 -> "Đang cập nhật"
//    value.signum() == 0 -> "Miễn phí"
//    else -> NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(value)
//}
//
//internal fun activityStatus(value: String?): String = when (value) {
//    "CONFIGURED" -> "Đã cấu hình"
//    "NOT_STARTED" -> "Chưa bắt đầu"
//    "IN_PROGRESS" -> "Đang diễn ra"
//    "COMPLETED" -> "Đã kết thúc"
//    "DELAYED" -> "Hoãn"
//    "CANCELLED" -> "Đã hủy"
//    else -> "Đang cập nhật"
//}
