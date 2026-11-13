package com.project.cruise.android.ui.chatbox.components


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Ba chấm nhấp nháy lần lượt, báo hiệu trợ lý đang soạn câu trả lời. */
@Composable
fun ChatTypingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp))
            .background(OceanPearl)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        repeat(3) { index ->
            val dotAlpha by
            transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 500, delayMillis = index * 160),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "typingDot$index",
            )
            Box(Modifier.size(8.dp).alpha(dotAlpha).clip(CircleShape).background(OceanTeal))
        }
    }
}