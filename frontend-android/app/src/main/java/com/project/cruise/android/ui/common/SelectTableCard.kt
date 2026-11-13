package com.project.cruise.android.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Card bấm được, đổi màu nền, viền và hiện dấu ✓ khi [selected]. */
@Composable
fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val container by
    animateColorAsState(
        if (selected) OceanMintSoft.copy(alpha = .6f) else Color.White,
        label = "selectableContainer",
    )
    val border by
    animateColorAsState(if (selected) OceanTeal else OceanLine, label = "selectableBorder")

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().semantics { this.selected = selected },
        shape = RoundedCornerShape(20.dp),
        color = container,
        border = BorderStroke(if (selected) 2.dp else 1.dp, border),
        shadowElevation = if (selected) 0.dp else 1.dp,
    ) {
        Box {
            Column(Modifier.padding(16.dp), content = content)
            if (selected)
                Box(
                    Modifier.align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(OceanTeal),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "✓",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
        }
    }
}