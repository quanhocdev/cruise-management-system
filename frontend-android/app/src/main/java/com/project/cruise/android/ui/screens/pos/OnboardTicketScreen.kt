package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.OnboardTicketState

@Composable
fun OnboardTicketScreen(state: OnboardTicketState, onBack: () -> Unit) {
    PosTheme {
        Column(
            Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Về ONBOARD POS") }
            PosBadge(PosRole.ONBOARD.title, PosRole.ONBOARD.accent())
            Text(
                "Kiểm tra vé lên tàu",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PosInk
            )

            when {
                state.loading -> PosPanel {
                    CircularProgressIndicator(color = PosRole.ONBOARD.accent())
                    Text("Đang đọc mã vé đã lưu…", color = PosMuted)
                }
                state.error != null -> PosPanel {
                    PosBadge("Không đọc được mã", MaterialTheme.colorScheme.error)
                    Text(state.error, color = PosMuted)
                }
                else -> {
                    PosPanel {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Mã QR vé", fontWeight = FontWeight.Bold, color = PosInk)
                            PosBadge("Chưa xác minh", Color(0xFF995417))
                        }
                        Text(maskTicketCode(state.qrValue.orEmpty()), color = PosInk)
                        Text(
                            "Mã đã được lưu cục bộ và chưa được gửi tới nghiệp vụ boarding.",
                            color = PosMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    PosPanel {
                        Text("Thông tin hành khách và chuyến", fontWeight = FontWeight.Bold, color = PosInk)
                        PendingTicketRow("Hành khách")
                        HorizontalDivider()
                        PendingTicketRow("Chuyến tàu")
                        HorizontalDivider()
                        PendingTicketRow("Trạng thái vé")
                    }

                    Surface(color = Color(0xFFFFEED0), shape = MaterialTheme.shapes.medium) {
                        Text(
                            "Backend hiện chưa có API ONBOARD nhận diện QR và xác nhận boarding. POS không tự suy đoán hành khách, chuyến hoặc trạng thái vé.",
                            Modifier.padding(14.dp),
                            color = Color(0xFF745014),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    ) { Text("Xác nhận khách lên tàu") }
                    Text(
                        "Nút sẽ được mở khi backend trả vé hợp lệ và cung cấp API xác nhận có chống gửi trùng.",
                        color = PosMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingTicketRow(label: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PosMuted)
        Text("Chờ API", color = Color(0xFF995417), fontWeight = FontWeight.SemiBold)
    }
}

private fun maskTicketCode(value: String): String = when {
    value.isBlank() -> "Không có dữ liệu"
    value.length <= 10 -> value
    else -> "${value.take(5)}••••${value.takeLast(5)}"
}
