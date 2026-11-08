package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

/** [selected] là tên của [BookingStatus], null = "Tất cả". */
@Composable
fun BookingStatusFilters(
    selected: String?,
    onSelect: (String?) -> Unit,
    resultCount: Int?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { StatusChip("Tất cả", selected == null) { onSelect(null) } }
            items(BookingStatus.entries.toList()) { status ->
                StatusChip(bookingLabel(status), selected == status.name) {
                    onSelect(status.name)
                }
            }
        }
        if (resultCount != null)
            Text(
                "$resultCount booking",
                color = OceanSlate,
                style = MaterialTheme.typography.labelLarge,
            )
    }
}

@Composable
private fun StatusChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(50),
        label = { Text(label) },
        colors =
            FilterChipDefaults.filterChipColors(
                containerColor = Color.White,
                labelColor = OceanSlate,
                selectedContainerColor = OceanNavy,
                selectedLabelColor = Color.White,
            ),
    )
}