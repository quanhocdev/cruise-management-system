package com.project.cruise.android.ui.passenger.components.messagedetail

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
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    canSend: Boolean,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...", color = OceanSlate) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = OceanTeal,
                    unfocusedBorderColor = OceanLine,
                    focusedTextColor = OceanNavy,
                    unfocusedTextColor = OceanNavy,
                    cursorColor = OceanTeal
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )

            Spacer(Modifier.width(8.dp))

            FilledIconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = OceanTeal,
                    contentColor = Color.White,
                    disabledContainerColor = OceanLine,
                    disabledContentColor = OceanSlate
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Gửi"
                    )
                }
            }
        }
    }
}