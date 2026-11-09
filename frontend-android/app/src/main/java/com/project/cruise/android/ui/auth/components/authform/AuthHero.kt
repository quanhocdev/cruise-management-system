package com.project.cruise.android.ui.auth.components.authform

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.R
import com.project.cruise.android.ui.guest.components.guestwelcome.EntranceAnimation
import com.project.cruise.android.ui.theme.*

@Composable
fun AuthHero(
    register: Boolean,
    onBack: () -> Unit,
    backEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(300.dp)) {
        Image(
            painter = painterResource(R.drawable.ocean_welcome_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, OceanPearl)))
        )

        FilledIconButton(
            onClick = onBack,
            enabled = backEnabled,
            modifier = Modifier.safeDrawingPadding().padding(20.dp).size(48.dp),
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = .88f),
                    contentColor = OceanNavy,
                ),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
        }

        EntranceAnimation(
            delayMillis = 0,
            modifier = Modifier.align(Alignment.BottomStart),
        ) {
            Column(
                Modifier.padding(horizontal = 28.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    "HẢI TRÌNH DI SẢN",
                    color = OceanTeal,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    if (register) "Tạo tài khoản" else "Chào mừng trở lại",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
                Text(
                    if (register) "Đăng ký để bắt đầu hành trình của bạn"
                    else "Hành trình của khách · Công việc của nhân viên",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OceanSlate,
                )
            }
        }
    }
}