package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.runtime.Composable
import com.project.cruise.android.ui.common.*

@Composable
fun BookingListEmpty(
    hasBookings: Boolean,
    canBrowseTours: Boolean,
    onBrowseTours: () -> Unit,
    onClearFilters: () -> Unit,
) {
    PassengerEmpty(
        "Chưa có booking phù hợp",
        if (hasBookings) "Thử mã khác hoặc đổi bộ lọc trạng thái."
        else "Khám phá các hành trình và chọn gói tour để bắt đầu kỳ nghỉ.",
        when {
            hasBookings -> "Xóa bộ lọc"
            canBrowseTours -> "Khám phá tour"
            else -> "Quay lại"
        },
        { if (hasBookings) onClearFilters() else onBrowseTours() },
    )
}