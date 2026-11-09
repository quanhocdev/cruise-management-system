package com.project.cruise.android.ui.passenger.components.mybooking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = OceanTeal,
                unfocusedBorderColor = OceanLine,
                focusedTextColor = OceanNavy,
                unfocusedTextColor = OceanNavy,
                cursorColor = OceanTeal,
            ),
        placeholder = {
            Text(
                "Tìm mã booking hoặc tên",
                color = OceanSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = { PassengerIcon(PassengerGlyph.SEARCH) },
        trailingIcon = {
            if (query.isNotEmpty())
                TextButton(onClick = { onQueryChange("") }) { Text("Xóa", color = OceanTeal) }
        },
    )
}