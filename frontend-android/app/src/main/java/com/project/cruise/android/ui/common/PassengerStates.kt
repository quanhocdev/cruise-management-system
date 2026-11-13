package com.project.cruise.android.ui.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.State
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Trạng thái đang tải dạng skeleton nhấp nháy, kèm dòng chữ báo hiệu. */
@Composable
fun PassengerLoading() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha =
        transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
            label = "skeletonAlpha",
        )

    PassengerCard {
        SkeletonBar(widthFraction = .45f, height = 22.dp, alpha = alpha)
        SkeletonBar(widthFraction = 1f, height = 14.dp, alpha = alpha)
        SkeletonBar(widthFraction = .8f, height = 14.dp, alpha = alpha)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(Modifier.size(18.dp), color = OceanTeal, strokeWidth = 2.dp)
            Text(
                "Đang tải thông tin…",
                color = OceanSlate,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SkeletonBar(widthFraction: Float, height: androidx.compose.ui.unit.Dp, alpha: State<Float>) {
    Box(
        Modifier.fillMaxWidth(widthFraction)
            .height(height)
            .graphicsLayer { this.alpha = alpha.value }
            .clip(RoundedCornerShape(8.dp))
            .background(OceanLine)
    )
}

@Composable
fun PassengerEmpty(
    title: String,
    message: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    PassengerCard {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(OceanMintSoft),
                contentAlignment = Alignment.Center,
            ) {
                PassengerIcon(PassengerGlyph.TICKET, modifier = Modifier.size(36.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
                textAlign = TextAlign.Center,
            )
            Text(message, color = OceanSlate, textAlign = TextAlign.Center)
            if (action != null && onAction != null) {
                Spacer(Modifier.height(4.dp))
                OceanPrimaryButton(action, onAction)
            }
        }
    }
}

@Composable
fun PassengerError(message: String, onRetry: () -> Unit) {
    PassengerCard {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(OceanCoral.copy(alpha = .12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanCoral,
                )
            }
            Text(
                "Chưa tải được thông tin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
                textAlign = TextAlign.Center,
            )
            Text(message, color = OceanSlate, textAlign = TextAlign.Center)
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTeal),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OceanTeal),
            ) {
                Text("Thử lại", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}