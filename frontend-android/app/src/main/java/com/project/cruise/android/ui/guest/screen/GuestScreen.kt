package com.project.cruise.android.ui.guest.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.guest.components.guestwelcome.*
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.ui.theme.OceanTheme

@Composable
fun GuestScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onPosClick: () -> Unit) {
    OceanTheme {
        Box(Modifier.fillMaxSize().background(OceanNavy)) {
            GuestBackdrop()
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .safeDrawingPadding()
                        .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EntranceAnimation(delayMillis = 100) { GuestBrandHeader() }

                Spacer(Modifier.weight(1f))

                EntranceAnimation(delayMillis = 350, modifier = Modifier.fillMaxWidth()) {
                    GuestHeadline()
                }
                Spacer(Modifier.height(18.dp))
                EntranceAnimation(delayMillis = 550, modifier = Modifier.fillMaxWidth()) {
                    GuestFeatureStrip()
                }
                Spacer(Modifier.height(20.dp))
                EntranceAnimation(delayMillis = 750, modifier = Modifier.fillMaxWidth()) {
                    GuestActionButtons(
                        onLoginClick = onLoginClick,
                        onRegisterClick = onRegisterClick,
                        onPosClick = onPosClick,
                    )
                }
            }
        }
    }
}