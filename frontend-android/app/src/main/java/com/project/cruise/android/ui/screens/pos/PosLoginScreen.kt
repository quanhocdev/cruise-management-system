package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun PosLoginScreen(onBackClick: () -> Unit, onLogin: (String, String) -> Unit, isLoading: Boolean, errorMessage: String?) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var revealPassword by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    fun submit() {
        if (username.isNotBlank() && password.isNotBlank() && !isLoading) {
            focus.clearFocus()
            onLogin(username.trim(), password)
        }
    }
    PosPage {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick, enabled = !isLoading, modifier = Modifier.semantics { contentDescription = "Quay lại" }) { PosGlyph(PosSymbol.BACK) }
            Text("CRUISE / POS", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            PosBadge("NHÂN VIÊN", PosRole.FINANCE.accent())
        }
        Surface(color = PosInk, shape = RoundedCornerShape(28.dp)) {
            Column(Modifier.fillMaxWidth().padding(26.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(56.dp).background(Color.White.copy(alpha=.12f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                    PosGlyph(PosSymbol.SHIP, Color(0xFF9DE1DE), Modifier.size(36.dp))
                }
                Text("Mỗi hành trình,\nmột trải nghiệm tốt hơn.", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Text("Không gian phục vụ dành cho đội ngũ du thuyền.", color = Color(0xFFBACDDC), style = MaterialTheme.typography.bodyMedium)
            }
        }
        PosPanel {
            Text("Chào mừng trở lại", style = MaterialTheme.typography.titleLarge)
            Text("Đăng nhập để bắt đầu phiên làm việc.", color = PosMuted, style = MaterialTheme.typography.bodyMedium)
            errorMessage?.let { PosNotice(it, warning = true) }
            OutlinedTextField(value = username, onValueChange = { username = it }, modifier = Modifier.fillMaxWidth(),
                label = { Text("Tên đăng nhập") }, leadingIcon = { PosGlyph(PosSymbol.PERSON, PosMuted) }, enabled = !isLoading,
                singleLine = true, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
            OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(),
                label = { Text("Mật khẩu") }, leadingIcon = { PosGlyph(PosSymbol.LOCK, PosMuted) }, enabled = !isLoading, singleLine = true,
                shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                visualTransformation = if (revealPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { revealPassword = !revealPassword }, enabled = !isLoading) { Text(if(revealPassword) "Ẩn" else "Hiện") } })
            PosPrimaryButton("Đăng nhập máy POS", PosRole.FINANCE, ::submit, username.isNotBlank() && password.isNotBlank(), isLoading)
        }
        Text("MỘT HỆ THỐNG · BỐN BỘ PHẬN", color = PosMuted, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PosRole.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { role ->
                        Surface(Modifier.weight(1f), color = Color.White, shape = RoundedCornerShape(16.dp)) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PosGlyph(when(role) { PosRole.FINANCE -> PosSymbol.QR; PosRole.CONVENIENCE -> PosSymbol.NFC; PosRole.ONBOARD -> PosSymbol.SHIP; PosRole.SHORE -> PosSymbol.COMPASS }, role.accent(), Modifier.size(20.dp))
                                Text(role.title, style = MaterialTheme.typography.labelMedium, color = PosInk, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
