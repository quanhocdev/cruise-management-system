package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

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