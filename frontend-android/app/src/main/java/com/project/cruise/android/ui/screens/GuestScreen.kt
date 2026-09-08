package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun GuestScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onPosClick: () -> Unit) {
    OceanPage {
        Text("HÀNH TRÌNH TRONG TẦM TAY", color = OceanTeal, style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.End))
        Spacer(Modifier.height(64.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ShipEmblem(Modifier.size(112.dp)) }
        Text("Cruise Management", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text("Hệ thống quản lý và trải nghiệm du thuyền", textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("—  ≋  —", color = OceanTeal, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(48.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text("Đăng nhập  →") }
        FilledTonalButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = OceanLavender, contentColor = OceanNavy)) { Text("Đăng ký") }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onPosClick, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Chế độ máy POS") }
        Text("Dành cho thiết bị vận hành đã đăng ký", textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
