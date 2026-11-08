package com.project.cruise.android.ui.passenger.components.passengermessages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.ConversationResponse
import com.project.cruise.android.ui.theme.*

@Composable
fun ConversationItem(
    conversation: ConversationResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(OceanMintSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = departmentIcon(conversation.department),
                    contentDescription = null,
                    tint = OceanTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = departmentLabel(conversation.department),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Booking: ${conversation.bookingCode ?: conversation.bookingId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OceanSlate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${conversation.status}",
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(OceanMintSoft)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OceanTeal
                    )
                    conversation.updatedAt?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = OceanSlate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = OceanSlate
            )
        }
    }
}