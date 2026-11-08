package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanTealBright
import com.project.cruise.android.ui.theme.ShipEmblem

@Composable
fun GuestBrandHeader(modifier: Modifier = Modifier) {
    // Logo tàu nhấp nhô nhẹ như đang trôi trên sóng
    val transition = rememberInfiniteTransition(label = "emblem")
    val bob by
    transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "emblemBob",
    )
    val tilt by
    transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec =
            infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "emblemTilt",
    )

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = OceanNavy.copy(alpha = .72f), shape = RoundedCornerShape(999.dp)) {
            Text(
                "VỊNH HẠ LONG · VIỆT NAM",
                Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                color = OceanMint,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(Modifier.height(28.dp))
        ShipEmblem(
            Modifier.size(88.dp).graphicsLayer {
                translationY = bob * density
                rotationZ = tilt
            }
        )
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "OceanCruise",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
            )
            Surface(color = OceanTealBright, shape = RoundedCornerShape(6.dp)) {
                Text(
                    "BOUTIQUE",
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Text(
            "MARITIME HOSPITALITY",
            color = Color.White.copy(alpha = .78f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}