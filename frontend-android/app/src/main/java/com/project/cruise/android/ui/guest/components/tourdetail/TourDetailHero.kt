package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.theme.*

/** Hero ảnh lớn: tên hành trình đè lên ảnh, trạng thái mở bán (vàng khi đang mở), mô tả thu gọn 3 dòng. */
@Composable
fun TourDetailHero(
    imageUrl: String?,
    statusLabel: String,
    isOpen: Boolean,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
        ) {
            PassengerIcon(
                PassengerGlyph.EXPLORE,
                OceanMint.copy(alpha = .25f),
                Modifier.align(Alignment.Center).size(72.dp),
            )
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(0.35f to Color.Transparent, 1f to OceanNavy.copy(alpha = .94f))
                )
            )
            Surface(
                Modifier.align(Alignment.TopStart).padding(14.dp),
                shape = RoundedCornerShape(50),
                color = if (isOpen) OceanGold else Color.White.copy(alpha = .92f),
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(if (isOpen) OceanNavy else OceanSlate))
                    Text(
                        statusLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                }
            }
            Text(
                title,
                Modifier.align(Alignment.BottomStart).padding(horizontal = 18.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = OceanSlate,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (description.length > 120) {
            Text(
                if (expanded) "Thu gọn" else "Xem thêm",
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { expanded = !expanded }.padding(vertical = 2.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = OceanGoldDeep,
            )
        }
    }
}