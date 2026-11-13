package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.runtime.Composable
import com.project.cruise.android.ui.common.PassengerEmpty
import com.project.cruise.android.ui.components.*

@Composable
fun TourCatalogEmpty(
    hasTours: Boolean,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
) {
    PassengerEmpty(
        "Chưa có hành trình phù hợp",
        if (hasTours) "Thử tên khác hoặc xem tất cả hành trình."
        else "Các chuyến đi sẽ xuất hiện khi được công bố.",
        if (hasTours) "Xóa bộ lọc" else "Tải lại",
        { if (hasTours) onClearFilters() else onRetry() },
    )
}