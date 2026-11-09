package com.project.cruise.android.ui.common

import com.project.cruise.android.data.dto.booking.BookingStatus
import com.project.cruise.android.data.dto.tour.TourBookingStatus

fun bookingLabel(status: BookingStatus?): String =
    when (status) {
        BookingStatus.PENDING_PAYMENT -> "Chờ thanh toán"
        BookingStatus.CONFIRMED -> "Đã xác nhận"
        BookingStatus.CANCELLED -> "Đã hủy"
        null -> "Chưa rõ trạng thái"
    }

/** Màu pill tương ứng trạng thái booking: xanh, vàng, đỏ. */
fun bookingTone(status: BookingStatus?): PillTone =
    when (status) {
        BookingStatus.CONFIRMED -> PillTone.POSITIVE
        BookingStatus.PENDING_PAYMENT -> PillTone.WARNING
        BookingStatus.CANCELLED -> PillTone.DANGER
        null -> PillTone.NEUTRAL
    }

fun tourSaleLabel(status: TourBookingStatus?): String =
    when (status) {
        TourBookingStatus.OPEN -> "Đang mở bán"
        TourBookingStatus.CLOSED -> "Đã đóng bán"
        TourBookingStatus.WAITING,
        TourBookingStatus.NOT_OPEN -> "Chưa mở bán"
        null -> "Chưa rõ trạng thái"
    }