package com.project.cruise.android.ui.pos.convenience.components.nfcscan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceAccent
import com.project.cruise.android.ui.pos.convenience.components.ConvenienceAccentSoft

private const val RING_COUNT = 3

/** Huy hiệu NFC ở giữa, có các vòng sóng lan tỏa khi đang chờ thẻ. */
@Composable
fun NfcScanIndicator(isResolving: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "nfcRings")
    val progress: List<State<Float>> =
        List(RING_COUNT) { index ->
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(2400, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(index * 800),
                    ),
                label = "nfcRing$index",
            )
        }

    Box(modifier.size(260.dp), contentAlignment = Alignment.Center) {
        if (!isResolving)
            Canvas(Modifier.fillMaxSize()) {
                val maxRadius = size.minDimension / 2f
                val minRadius = maxRadius * 0.42f
                progress.forEach { p ->
                    val t = p.value
                    drawCircle(
                        color = ConvenienceAccent.copy(alpha = (1f - t) * 0.35f),
                        radius = minRadius + (maxRadius - minRadius) * t,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                }
            }

        Box(
            Modifier.size(120.dp).clip(CircleShape).background(ConvenienceAccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            if (isResolving)
                CircularProgressIndicator(color = ConvenienceAccent)
            else
                Text(
                    "NFC",
                    color = ConvenienceAccent,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )
        }
    }
}