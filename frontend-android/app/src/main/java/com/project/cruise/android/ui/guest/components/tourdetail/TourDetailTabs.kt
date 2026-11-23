package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

data class TourTabUi(val label: String, val count: Int? = null)

/** Hàng tab cuộn ngang; tab đang chọn nền vàng, kèm số lượng nếu có. */
@Composable
fun TourDetailTabRow(
    tabs: List<TourTabUi>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        itemsIndexed(tabs, key = { _, tab -> tab.label }) { index, tab ->
            val on = index == selected
            val bg by animateColorAsState(if (on) OceanGold else Color.White, label = "tourTabBg")
            Surface(
                onClick = { onSelect(index) },
                modifier = Modifier.semantics { this.selected = on },
                shape = RoundedCornerShape(50),
                color = bg,
                border = BorderStroke(1.dp, if (on) OceanGold else OceanLine),
                shadowElevation = if (on) 3.dp else 0.dp,
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = OceanNavy,
                    )
                    tab.count?.let {
                        Surface(shape = RoundedCornerShape(50), color = if (on) OceanNavy else OceanGoldSoft) {
                            Text(
                                it.toString(),
                                Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (on) OceanGold else OceanGoldDeep,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Ba ô thông tin nhanh ngay dưới hero: khởi hành (vàng đậm), kết thúc, sức chứa hoặc số ngày. */
@Composable
fun TourFactsRow(
    startDate: String,
    endDate: String,
    lastLabel: String,
    lastValue: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FactTile("Khởi hành", startDate, Modifier.weight(1.25f), highlight = true)
        FactTile("Kết thúc", endDate, Modifier.weight(1.25f))
        FactTile(lastLabel, lastValue, Modifier.weight(1f))
    }
}

@Composable
private fun FactTile(label: String, value: String, modifier: Modifier, highlight: Boolean = false) {
    Surface(
        modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (highlight) OceanGold else OceanGoldSoft,
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
                maxLines = 1,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) OceanNavy.copy(alpha = .75f) else OceanGoldDeep,
            )
        }
    }
}