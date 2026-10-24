package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.OnboardTicketState

@Composable
fun OnboardTicketScreen(state: OnboardTicketState, onBack: () -> Unit) {
    val role = PosRole.ONBOARD
    PosPage {
        PosTopBar("Kiểm tra vé khách", role, onBack)
        when {
            state.loading -> PosPanel { CircularProgressIndicator(color = role.accent()); Text("Đang đọc mã vé…", color = PosMuted) }
            state.error != null -> PosPanel { PosBadge("Không đọc được mã", MaterialTheme.colorScheme.error); Text(state.error, color = PosMuted) }
            else -> {
                PosPanel {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(Modifier.size(56.dp).background(role.accent().copy(alpha=.10f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) { PosGlyph(PosSymbol.QR, role.accent(), Modifier.size(32.dp)) }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text("Vé điện tử", fontWeight = FontWeight.Bold); PosBadge("Chưa xác minh", PosAmber) }
                    }
                    HorizontalDivider()
                    PosDetailRow("Mã đã đọc", posDisplayCode(state.qrValue.orEmpty()))
                    Text("Mã đã lưu trên thiết bị", style = MaterialTheme.typography.bodySmall, color = PosMuted)
                }
                Text("Thông tin đối chiếu", style = MaterialTheme.typography.titleLarge)
                PosPanel {
                    PosDetailRow("Hành khách", "Chưa được xác minh")
                    HorizontalDivider()
                    PosDetailRow("Chuyến tàu", "Chưa được xác minh")
                    HorizontalDivider()
                    PosDetailRow("Trạng thái vé", "Chưa có kết quả kiểm tra")
                }
                PosNotice("Chưa thể xác nhận khách lên tàu. Đối chiếu vé với người phụ trách trước khi tiếp tục.", warning = true)
                PosPrimaryButton("Xác nhận khách lên tàu", role, {}, enabled = false)
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp)) { Text("Về tác vụ để quét vé tiếp theo") }
            }
        }
    }
}
