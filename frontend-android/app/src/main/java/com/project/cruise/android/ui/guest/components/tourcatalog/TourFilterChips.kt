package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.ui.theme.*

enum class TripFilterOption(val apiValue: String?, val label: String) {
    ALL(null, "Tất cả"),
    READY("READY", "Sắp diễn ra"),
    IN_PROGRESS("IN_PROGRESS", "Đang diễn ra"),
    COMPLETED("COMPLETED", "Đã hoàn thành"),
}

enum class BookingFilterOption(val apiValue: String?, val label: String) {
    ALL(null, "Tất cả"),
    OPEN("OPEN", "Đang mở bán"),
    WAITING("WAITING", "Sắp mở bán"),
    NOT_OPEN("NOT_OPEN", "Chưa mở bán"),
    CLOSED("CLOSED", "Đã đóng"),
}

private fun countTrip(tours: List<PublicTourSummaryResponse>, option: TripFilterOption): Int =
    if (option == TripFilterOption.ALL) {
        tours.size
    } else {
        tours.count { it.statusTrip?.toString().equals(option.apiValue, ignoreCase = true) }
    }

private fun countBooking(tours: List<PublicTourSummaryResponse>, option: BookingFilterOption): Int =
    if (option == BookingFilterOption.ALL) {
        tours.size
    } else {
        tours.count { it.statusBooking?.toString().equals(option.apiValue, ignoreCase = true) }
    }

@Composable
fun TourFilterChips(
    tours: List<PublicTourSummaryResponse>,
    loading: Boolean,
    shownCount: Int,
    selectedTrip: TripFilterOption,
    selectedBooking: BookingFilterOption,
    onTripChange: (TripFilterOption) -> Unit,
    onBookingChange: (BookingFilterOption) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasFilter = selectedTrip != TripFilterOption.ALL || selectedBooking != BookingFilterOption.ALL

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Hành trình",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = OceanSlate,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TripFilterOption.entries.forEach { option ->
                    val countText = if (loading) "" else " (${countTrip(tours, option)})"
                    FilterOption(
                        label = "${option.label}$countText",
                        selected = selectedTrip == option,
                        onClick = { onTripChange(option) },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Trạng thái vé",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = OceanSlate,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BookingFilterOption.entries.forEach { option ->
                    val countText = if (loading) "" else " (${countBooking(tours, option)})"
                    FilterOption(
                        label = "${option.label}$countText",
                        selected = selectedBooking == option,
                        onClick = { onBookingChange(option) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (loading) {
                    "Đang tải tour..."
                } else {
                    "Hiển thị $shownCount / ${tours.size} tour"
                },
                style = MaterialTheme.typography.bodySmall,
                color = OceanSlate,
            )

            if (hasFilter) {
                TextButton(onClick = onReset) {
                    Text(
                        text = "Xóa bộ lọc",
                        style = MaterialTheme.typography.labelLarge,
                        color = OceanNavy,
                    )
                }
            }
        }
    }
}

@Composable
fun TourFilterChips(
    openOnly: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterOption("Tất cả", selected = !openOnly) { onChange(false) }
        FilterOption("Đang mở bán", selected = openOnly) { onChange(true) }
    }
}

@Composable
private fun FilterOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(50),
        label = { Text(label, maxLines = 1) },
        colors =
            FilterChipDefaults.filterChipColors(
                containerColor = Color.White,
                labelColor = OceanSlate,
                selectedContainerColor = OceanNavy,
                selectedLabelColor = Color.White,
            ),
    )
}