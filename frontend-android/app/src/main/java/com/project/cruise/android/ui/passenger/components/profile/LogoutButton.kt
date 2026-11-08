package com.project.cruise.android.ui.passenger.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun LogoutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 54.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = OceanCoral),
        border = BorderStroke(1.dp, OceanCoral.copy(alpha = .5f)),
        shape = RoundedCornerShape(18.dp),
    ) {
        PassengerIcon(PassengerGlyph.EXIT, OceanCoral)
        Spacer(Modifier.width(10.dp))
        Text("Đăng xuất", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LogoutConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = { Text("Đăng xuất tài khoản?", fontWeight = FontWeight.Bold, color = OceanNavy) },
        text = {
            Text(
                "Bạn có thể đăng nhập lại để xem các chuyến đi của mình.",
                color = OceanSlate,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanCoral, contentColor = Color.White),
            ) {
                Text("Đăng xuất", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ở lại", color = OceanSlate) } },
    )
}