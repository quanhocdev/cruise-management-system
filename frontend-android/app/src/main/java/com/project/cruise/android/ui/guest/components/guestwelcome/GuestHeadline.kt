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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanMint

@Composable
fun GuestHeadline(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Surface(color = Color.White.copy(alpha = .18f), shape = RoundedCornerShape(999.dp)) {
            Text(
                "◇  TRẢI NGHIỆM DU THUYỀN",
                Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "Hành Trình Diệu Kỳ",
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Giữa Lòng Đại Dương",
            color = OceanMint,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Khám phá hành trình, quản lý booking và trải nghiệm kỳ nghỉ trên biển.",
            color = Color.White.copy(alpha = .88f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}