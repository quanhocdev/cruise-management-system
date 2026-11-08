package com.project.cruise.android.ui.passenger.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun ProfileHeroCard(
    username: String,
    role: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 28.dp, horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(88.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = .16f)),
                contentAlignment = Alignment.Center,
            ) {
                PassengerIcon(PassengerGlyph.PERSON, OceanMint, Modifier.size(44.dp))
            }
            Text(
                username,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                roleLabel(role),
                modifier =
                    Modifier.clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = .16f))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanMint,
            )
            Text(
                "Chào mừng bạn trở lại OceanCruise.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = .85f),
            )
        }
    }
}

private fun roleLabel(role: String): String =
    role.removePrefix("ROLE_").let { if (it == "PASSENGER") "Hành khách" else it }