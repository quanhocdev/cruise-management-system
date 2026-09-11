package com.project.cruise.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.R
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanTealBright
import com.project.cruise.android.ui.theme.OceanTheme
import com.project.cruise.android.ui.theme.ShipEmblem

@Composable
fun GuestScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onPosClick: () -> Unit) {
    OceanTheme {
        Box(Modifier.fillMaxSize().background(OceanNavy)) {
            Image(
                painter = painterResource(R.drawable.ocean_welcome_hero),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0f to OceanNavy.copy(alpha = .30f),
                        .48f to OceanNavy.copy(alpha = .45f),
                        1f to OceanNavy.copy(alpha = .98f)
                    )
                )
            )
            Column(
                modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(color = OceanNavy.copy(alpha = .72f), shape = RoundedCornerShape(999.dp)) {
                    Text("VỊNH HẠ LONG · VIỆT NAM", Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                        color = OceanMint, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(28.dp))
                ShipEmblem(Modifier.size(88.dp))
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("OceanCruise", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    Surface(color = OceanTealBright, shape = RoundedCornerShape(6.dp)) {
                        Text("BOUTIQUE", Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text("MARITIME HOSPITALITY", color = Color.White.copy(alpha = .78f),
                    style = MaterialTheme.typography.labelMedium)

                Spacer(Modifier.weight(1f))

                Surface(color = Color.White.copy(alpha = .18f), shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.align(Alignment.Start)) {
                    Text("◇  TRẢI NGHIỆM DU THUYỀN", Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(18.dp))
                Text("Hành Trình Diệu Kỳ", color = Color.White, style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Text("Giữa Lòng Đại Dương", color = OceanMint, style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text(
                    "Khám phá hành trình, quản lý booking và trải nghiệm kỳ nghỉ trên biển.",
                    color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(OceanNavy.copy(alpha = .80f))
                    .padding(vertical = 14.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FeatureLabel("TOUR", "Khám phá")
                    FeatureLabel("BOOKING", "Giữ chỗ")
                    FeatureLabel("VNPAY", "Sandbox")
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanTealBright, contentColor = Color.White)) {
                    Text("Đăng nhập   →", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = .12f), contentColor = Color.White)) {
                    Text("Tạo tài khoản", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(14.dp))
                TextButton(onClick = onPosClick, modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = .74f))) {
                    Text("▣  Truy cập dành cho máy POS")
                }
            }
        }
    }
}

@Composable
private fun FeatureLabel(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = OceanMint, style = MaterialTheme.typography.labelLarge)
        Text(subtitle, color = Color.White, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}
