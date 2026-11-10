package com.project.cruise.android.ui.pos.convenience.components.usage

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceAccent
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceAccentSoft

enum class ConvenienceUsageType {
    PRODUCT,
    SERVICE
}

/** Bộ chuyển dạng segmented: Sản phẩm | Dịch vụ. */
@Composable
fun ConvenienceTypeSelector(
    selected: ConvenienceUsageType,
    onSelect: (ConvenienceUsageType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ConvenienceAccentSoft)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Segment("Sản phẩm", selected == ConvenienceUsageType.PRODUCT, Modifier.weight(1f)) {
            onSelect(ConvenienceUsageType.PRODUCT)
        }
        Segment("Dịch vụ", selected == ConvenienceUsageType.SERVICE, Modifier.weight(1f)) {
            onSelect(ConvenienceUsageType.SERVICE)
        }
    }
}

@Composable
private fun Segment(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val container by
    animateColorAsState(if (selected) ConvenienceAccent else Color.Transparent, label = "segBg")
    val content by
    animateColorAsState(
        if (selected) Color.White else ConvenienceAccent,
        label = "segFg",
    )
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = container,
    ) {
        Box(Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                color = content,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}