package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.project.cruise.android.R
import com.project.cruise.android.ui.theme.OceanNavy

/** Ảnh nền + lớp gradient phủ tối dần xuống dưới để chữ dễ đọc. */
@Composable
fun GuestBackdrop(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ocean_welcome_hero),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
    Box(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to OceanNavy.copy(alpha = .30f),
                    .48f to OceanNavy.copy(alpha = .45f),
                    1f to OceanNavy.copy(alpha = .98f),
                )
            )
    )
}