package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingTextField(
    label: String,
    value: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = OceanMintSoft.copy(alpha = .25f),
                unfocusedBorderColor = OceanLine,
                focusedBorderColor = OceanTeal,
                focusedLabelColor = OceanTeal,
                unfocusedLabelColor = OceanSlate,
                focusedTextColor = OceanNavy,
                unfocusedTextColor = OceanNavy,
                cursorColor = OceanTeal,
            ),
        modifier = Modifier.fillMaxWidth(),
    )
}