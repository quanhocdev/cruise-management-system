package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.common.CruiseVisual
import com.project.cruise.android.ui.common.PassengerPill
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun TourDetailHero(
    imageUrl: String?,
    statusLabel: String,
    isOpen: Boolean,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CruiseVisual(imageUrl)
        PassengerPill(statusLabel, isOpen)
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Text(description, style = MaterialTheme.typography.bodyLarge, color = OceanSlate)
    }
}