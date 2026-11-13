package com.project.cruise.android.ui.chatbox.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    canSend: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxWidth(), color = Color.White, shadowElevation = 6.dp) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...", color = OceanSlate) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = OceanPearl,
                        focusedBorderColor = OceanTeal,
                        unfocusedBorderColor = OceanLine,
                        focusedTextColor = OceanNavy,
                        unfocusedTextColor = OceanNavy,
                        cursorColor = OceanTeal,
                    ),
            )
            FilledIconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier.size(48.dp),
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = OceanTeal,
                        contentColor = Color.White,
                        disabledContainerColor = OceanLine,
                        disabledContentColor = OceanSlate,
                    ),
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi")
            }
        }
    }
}