package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanTealBright
import com.project.cruise.android.ui.theme.ShipEmblem

@Composable
fun GuestBrandHeader(modifier: Modifier = Modifier) {
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
        ShipEmblem(Modifier.size(88.dp))
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