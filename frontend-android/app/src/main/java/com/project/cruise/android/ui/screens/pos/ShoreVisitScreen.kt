package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.network.ShoreVisitTourResponse
import com.project.cruise.android.viewmodel.pos.ShoreVisitState

@Composable
fun ShoreVisitScreen(state: ShoreVisitState, onRetry: () -> Unit, onBack: () -> Unit) {
    var selectedTourId by rememberSaveable { mutableStateOf<String?>(null) }
    PosTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TextButton(onClick = onBack) { Text("← Về SHORE POS") } }
            item { PosBadge(PosRole.SHORE.title, PosRole.SHORE.accent()) }
            item {
                Text(
                    "Kiểm tra khách tham quan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PosInk
                )
            }
            item { ShoreQrPanel(state.qrValue) }

            when {
                state.loading -> item {
                    PosPanel {
                        CircularProgressIndicator(color = PosRole.SHORE.accent())
                        Text("Đang tải danh sách chuyến tham quan…", color = PosMuted)
                    }
                }
                state.error != null -> item {
                    PosPanel {
                        Text("Không tải được chuyến tham quan", fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error)
                        Text(state.error, color = PosMuted)
                        Button(onClick = onRetry) { Text("Thử lại") }
                    }
                }
                else -> {
                    item {
                        Text("Chọn chuyến tham quan", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium, color = PosInk)
                    }
                    if (state.visitTours.isEmpty()) {
                        item { PosPanel { Text("Backend chưa có chuyến tham quan bờ.", color = PosMuted) } }
                    } else {
                        items(state.visitTours, key = { it.id }) { tour ->
                            ShoreTourCard(
                                tour = tour,
                                selected = selectedTourId == tour.id,
                                onSelect = { selectedTourId = tour.id }
                            )
                        }
                    }

                    item {
                        PosPanel {
                            Text("Hành khách và đăng ký", fontWeight = FontWeight.Bold, color = PosInk)
                            ShorePendingRow("Hành khách")
                            HorizontalDivider()
                            ShorePendingRow("Trạng thái đăng ký")
                            HorizontalDivider()
                            ShorePendingRow("Trạng thái đi / về")
                        }
                    }
                    item {
                        Surface(color = Color(0xFFFFEED0), shape = MaterialTheme.shapes.medium) {
                            Text(
                                "Backend chưa có API nhận diện khách và điểm danh SHORE. POS không tự suy đoán đăng ký hoặc đánh dấu khách đã rời tàu/quay lại.",
                                Modifier.padding(14.dp), color = Color(0xFF745014),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {}, enabled = false,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                            ) { Text("Xác nhận khách rời tàu") }
                            OutlinedButton(
                                onClick = {}, enabled = false,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                            ) { Text("Xác nhận khách đã quay lại") }
                            Text(
                                if (selectedTourId == null) "Chọn chuyến tham quan trước khi kiểm tra khách."
                                else "Đã chọn chuyến. Chờ API điểm danh từ backend để tiếp tục.",
                                color = PosMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoreQrPanel(qrValue: String?) {
    if (qrValue == null) return
    PosPanel {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mã QR khách", fontWeight = FontWeight.Bold, color = PosInk)
            PosBadge("Chưa xác minh", Color(0xFF995417))
        }
        Text(maskShoreCode(qrValue), color = PosInk)
        Text("Mã đã được lưu cục bộ và chưa ghi nhận điểm danh.", color = PosMuted,
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ShoreTourCard(tour: ShoreVisitTourResponse, selected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        color = if (selected) PosRole.SHORE.accent().copy(alpha = .10f) else Color.White,
        shape = MaterialTheme.shapes.large,
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, PosRole.SHORE.accent()) else null
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RadioButton(selected = selected, onClick = onSelect)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(tour.name, Modifier.weight(1f), fontWeight = FontWeight.Bold, color = PosInk)
                    tour.status?.let { PosBadge(it, PosRole.SHORE.accent()) }
                }
                tour.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = PosMuted, style = MaterialTheme.typography.bodySmall)
                }
                val time = listOfNotNull(tour.startTime?.let(::shoreTime), tour.endTime?.let(::shoreTime))
                    .joinToString(" → ")
                if (time.isNotBlank()) Text(time, color = PosMuted, style = MaterialTheme.typography.bodySmall)
                tour.maxPassengers?.let {
                    Text("Tối đa $it khách", color = PosMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ShorePendingRow(label: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PosMuted)
        Text("Chờ API", color = Color(0xFF995417), fontWeight = FontWeight.SemiBold)
    }
}

private fun shoreTime(value: String): String = value.replace('T', ' ').take(16)

private fun maskShoreCode(value: String): String = when {
    value.length <= 10 -> value
    else -> "${value.take(5)}••••${value.takeLast(5)}"
}
