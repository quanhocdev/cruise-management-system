package com.project.cruise.android.ui.auth.components.authform

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
private fun authFieldColors() =
    OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = OceanPearl,
        focusedContainerColor = Color.White,
        unfocusedBorderColor = OceanLine,
        focusedBorderColor = OceanTeal,
        focusedLabelColor = OceanTeal,
        unfocusedLabelColor = OceanSlate,
        focusedLeadingIconColor = OceanTeal,
        unfocusedLeadingIconColor = OceanSlate,
        focusedTextColor = OceanNavy,
        unfocusedTextColor = OceanNavy,
        cursorColor = OceanTeal,
    )

@Composable
fun AuthTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        colors = authFieldColors(),
    )
}

@Composable
fun AuthPasswordField(
    value: String,
    onChange: (String) -> Unit,
    revealed: Boolean,
    onToggleReveal: () -> Unit,
    enabled: Boolean,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Mật khẩu") },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        visualTransformation =
            if (revealed) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        trailingIcon = {
            TextButton(onClick = onToggleReveal, enabled = enabled) {
                Text(if (revealed) "Ẩn" else "Hiện", color = OceanTeal)
            }
        },
        colors = authFieldColors(),
    )
}