package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Nhóm chip chọn một giá trị: options là danh sách (giá trị gửi đi -> nhãn hiển thị). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceChips(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    enabled: Boolean,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = OceanSlate)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { (value, text) ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    enabled = enabled,
                    shape = RoundedCornerShape(50),
                    label = { Text(text) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            labelColor = OceanSlate,
                            selectedContainerColor = OceanNavy,
                            selectedLabelColor = Color.White,
                        ),
                )
            }
        }
    }
}