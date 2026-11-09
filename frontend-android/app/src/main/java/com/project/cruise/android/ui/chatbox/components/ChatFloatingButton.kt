package com.project.cruise.android.ui.chatbox.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.theme.OceanTeal

/** Nút chat nổi, nhịp nhẹ để thu hút sự chú ý mà không gây khó chịu. */
@Composable
fun ChatFloatingButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "chatFabPulse")
    val scale by
    transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec =
            infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "chatFabScale",
    )

    FloatingActionButton(
        onClick = onClick,
        modifier =
            modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        containerColor = OceanTeal,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
    ) {
        PassengerIcon(PassengerGlyph.MESSAGE, Color.White, Modifier.size(28.dp))
    }
}