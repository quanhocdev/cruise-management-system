package com.project.cruise.android.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/**
 * Hiển thị một dòng chữ phụ chỉ khi [value] khác null và không rỗng.
 * Kết quả là "$prefix$value$suffix", kèm khoảng cách [topSpace] phía trên.
 * [emphasized] = true dùng cho dòng giá (chữ đậm, màu teal).
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OceanTeal
        )
    } else {
        Text(
            text = "$prefix$text$suffix",
            modifier = modifier,
            style = style,
            color = OceanSlate
        )
    }
}