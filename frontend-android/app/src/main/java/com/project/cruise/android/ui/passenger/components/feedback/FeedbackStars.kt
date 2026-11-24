package com.project.cruise.android.ui.passenger.components.feedback

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

fun ratingLabel(rating: Int): String =
    when (rating) {
        1 -> "Chưa hài lòng"
        2 -> "Tạm được"
        3 -> "Bình thường"
        4 -> "Hài lòng"
        5 -> "Tuyệt vời!"
        else -> "Chạm vào sao để chọn"
    }

/**
 * Hàng 5 ngôi sao. [onRatingChange] = null thì chỉ hiển thị; có thì chạm sao nào chọn sao đó
 * (sao được chọn phóng nhẹ, nảy lò xo).
 */
@Composable
fun StarRatingRow(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 28.dp,
    spacing: Dp = 2.dp,
    inactiveColor: Color = OceanLine,
    onRatingChange: ((Int) -> Unit)? = null,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(spacing)) {
        (1..5).forEach { value ->
            val active = value <= rating
            val scale by animateFloatAsState(
                if (active) 1.12f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "starScale",
            )
            val tint by animateColorAsState(if (active) OceanGold else inactiveColor, label = "starTint")
            val clickable = if (onRatingChange != null) {
                Modifier.clip(CircleShape).clickable(role = Role.Button) { onRatingChange(value) }.padding(2.dp)
            } else Modifier
            Icon(
                Icons.Filled.Star,
                contentDescription = if (onRatingChange != null) "$value sao" else null,
                tint = tint,
                modifier = clickable.size(starSize).graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            )
        }
    }
}