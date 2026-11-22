package com.project.cruise.android.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.project.cruise.android.R
import com.project.cruise.android.ui.theme.*

@Composable
fun PassengerDiscoveryHero() {
    // Ảnh nền phóng to rất chậm (Ken Burns) để hero có chiều sâu
    val transition = rememberInfiniteTransition(label = "heroZoom")
    val zoom by
    transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "heroZoomValue",
    )

    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(OceanNavy)) {
        Image(
            painterResource(R.drawable.ocean_welcome_hero),
            null,
            Modifier.matchParentSize().graphicsLayer {
                scaleX = zoom
                scaleY = zoom
            },
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier.matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(OceanNavy.copy(alpha = .24f), OceanNavy.copy(alpha = .94f))
                    )
                )
        )
        Column(
            Modifier.padding(18.dp).heightIn(min = 148.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(color = Color.White.copy(alpha = .16f), shape = RoundedCornerShape(50)) {
                Text(
                    "KHÁM PHÁ TOUR DU THUYỀN",
                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = OceanMint,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Kỳ nghỉ trên biển, bắt đầu từ đây.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                "Du thuyền hiện đại - Lịch trình rõ ràng.",
                color = Color.White.copy(alpha = .9f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun CruiseVisual(imageUrl: String?, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(144.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        // Nền sóng + logo tàu hiện khi chưa có ảnh (hoặc ảnh đang tải)
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0..5) {
                val path =
                    Path().apply {
                        moveTo(-30f, size.height * .68f + i * 14f)
                        cubicTo(
                            size.width * .3f,
                            size.height * .42f + i * 14f,
                            size.width * .7f,
                            size.height * .95f + i * 14f,
                            size.width + 30f,
                            size.height * .60f + i * 14f,
                        )
                    }
                drawPath(path, OceanMint.copy(alpha = .15f), style = Stroke(2f))
            }
        }
        ShipEmblem(Modifier.align(Alignment.Center).size(72.dp))

        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = "Ảnh du thuyền",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Lớp tối nhẹ ở đáy ảnh cho cảm giác có chiều sâu
            Box(
                Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.6f to Color.Transparent,
                            1f to OceanNavy.copy(alpha = .35f),
                        )
                    )
            )
        }
    }
}