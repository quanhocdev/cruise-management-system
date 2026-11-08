package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.project.cruise.android.R
import com.project.cruise.android.ui.theme.OceanNavy

/**
 * Ảnh nền phóng to chậm qua lại (hiệu ứng Ken Burns) + lớp gradient phủ
 * tối dần xuống dưới để chữ dễ đọc.
 */
@Composable
fun GuestBackdrop(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "backdrop")
    val scale by
    transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing), RepeatMode.Reverse),
        label = "backdropScale",
    )

    Image(
        painter = painterResource(R.drawable.ocean_welcome_hero),
        contentDescription = null,
        modifier =
            modifier.fillMaxSize().graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentScale = ContentScale.Crop,
    )
    Box(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to OceanNavy.copy(alpha = .30f),
                    .48f to OceanNavy.copy(alpha = .45f),
                    1f to OceanNavy.copy(alpha = .98f),
                )
            )
    )
}