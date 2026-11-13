package com.project.cruise.android.ui.pos.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.OptionalInfoText
import com.project.cruise.android.ui.common.PassengerPill
import com.project.cruise.android.ui.common.PillTone
import com.project.cruise.android.ui.common.SelectableCard
import com.project.cruise.android.ui.theme.OceanMintSoft
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanSlate

/**
 * Card chọn hoạt động dùng chung cho các màn POS (trên tàu, tham quan bờ):
 * tên + mô tả, các thông tin phụ dạng chip, giá và trạng thái ở hàng cuối.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityInfoCard(
    title: String,
    description: Any?,
    facts: List<String>,
    price: Any?,
    status: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableCard(selected = selected, onClick = onClick) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )

        OptionalInfoText(value = description, style = MaterialTheme.typography.bodyMedium)

        if (facts.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                facts.forEach { FactChip(it) }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                // OptionalInfoText không phát ra gì khi price rỗng
                OptionalInfoText(price, prefix = "Giá: ", topSpace = 0.dp, emphasized = true)
            }
            status?.takeIf { it.isNotBlank() }?.let { PassengerPill(it, PillTone.NEUTRAL) }
        }
    }
}

@Composable
private fun FactChip(text: String) {
    Surface(color = OceanMintSoft.copy(alpha = .6f), shape = RoundedCornerShape(10.dp)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = OceanSlate,
        )
    }
}

/** "Nhãn: giá trị" nếu giá trị không rỗng, ngược lại null. */
fun Any?.toFact(label: String, suffix: String = ""): String? =
    this?.toString()?.takeIf { it.isNotBlank() }?.let { "$label: $it$suffix" }