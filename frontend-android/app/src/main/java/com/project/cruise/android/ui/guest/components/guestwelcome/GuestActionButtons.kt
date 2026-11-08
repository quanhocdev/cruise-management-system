package com.project.cruise.android.ui.guest.components.guestwelcome

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanTealBright

@Composable
fun GuestActionButtons(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onPosClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val loginSource = remember { MutableInteractionSource() }
    val registerSource = remember { MutableInteractionSource() }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onLoginClick,
            interactionSource = loginSource,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp).pressScale(loginSource),
            shape = RoundedCornerShape(20.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = OceanTealBright,
                    contentColor = Color.White,
                ),
        ) {
            Text("Đăng nhập   →", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRegisterClick,
            interactionSource = registerSource,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp).pressScale(registerSource),
            shape = RoundedCornerShape(20.dp),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = .12f),
                    contentColor = Color.White,
                ),
        ) {
            Text("Tạo tài khoản", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(14.dp))
        TextButton(
            onClick = onPosClick,
            modifier = Modifier.heightIn(min = 48.dp),
            colors =
                ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = .74f)),
        ) {
            Text("▣  Truy cập dành cho máy POS")
        }
    }
}