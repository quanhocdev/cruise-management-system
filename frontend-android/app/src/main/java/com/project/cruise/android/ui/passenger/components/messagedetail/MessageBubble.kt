package com.project.cruise.android.ui.passenger.components.messagedetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.MessageResponse
import com.project.cruise.android.ui.theme.*

@Composable
fun MessageBubble(
    message: MessageResponse,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 290.dp),
            color = if (isMine) OceanNavy else Color.White,
            border = if (isMine) null else BorderStroke(1.dp, OceanLine),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 18.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = message.content,
                    color = if (isMine) Color.White else OceanNavy,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )

                message.sentAt?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        color = if (isMine) OceanMint else OceanSlate,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}