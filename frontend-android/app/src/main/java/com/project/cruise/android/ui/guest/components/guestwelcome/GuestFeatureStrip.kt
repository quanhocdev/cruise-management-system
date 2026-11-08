package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy

@Composable
fun GuestFeatureStrip(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OceanNavy.copy(alpha = .80f))
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        FeatureLabel("TOUR", "Khám phá")
        FeatureLabel("BOOKING", "Giữ chỗ")
        FeatureLabel("VNPAY", "Sandbox")
    }
}

@Composable
private fun FeatureLabel(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = OceanMint, style = MaterialTheme.typography.labelLarge)
        Text(
            subtitle,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}