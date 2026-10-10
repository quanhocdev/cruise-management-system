package com.project.cruise.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Card bấm được, đổi màu khi [selected]. */
@Composable
fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Hiển thị một dòng chữ phụ chỉ khi [value] khác null và không rỗng.
 * Kết quả là "$prefix$value$suffix", kèm khoảng cách [topSpace] phía trên.
 * [emphasized] = true dùng cho dòng giá (chữ đậm, màu mặc định).
 */
@Composable
fun OptionalInfoText(
    value: Any?,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    topSpace: Dp = 4.dp,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    emphasized: Boolean = false
) {
    val text = value?.toString()?.takeIf { it.isNotBlank() } ?: return

    Spacer(modifier = Modifier.height(topSpace))

    if (emphasized) {
        Text(
            text = "$prefix$text$suffix",
            modifier = modifier,
            style = LocalTextStyle.current,
            fontWeight = FontWeight.SemiBold
        )
    } else {
        Text(
            text = "$prefix$text$suffix",
            modifier = modifier,
            style = style,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}