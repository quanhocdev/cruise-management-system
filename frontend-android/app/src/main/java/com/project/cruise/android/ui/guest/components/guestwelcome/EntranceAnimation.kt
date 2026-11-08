package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Hiện nội dung bằng hiệu ứng mờ dần + trượt nhẹ từ dưới lên khi màn hình mở.
 * Dùng [delayMillis] khác nhau cho từng khối để tạo hiệu ứng xuất hiện lần lượt (stagger).
 */
@Composable
fun EntranceAnimation(
    delayMillis: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter =
            fadeIn(tween(durationMillis = 650, delayMillis = delayMillis)) +
                    slideInVertically(
                        animationSpec =
                            tween(
                                durationMillis = 650,
                                delayMillis = delayMillis,
                                easing = FastOutSlowInEasing,
                            ),
                        initialOffsetY = { it / 5 },
                    ),
    ) {
        content()
    }
}

/** Thu nhỏ nhẹ khi nhấn giữ, nảy lại khi thả. */
@Composable
fun Modifier.pressScale(interactionSource: MutableInteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by
    animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}