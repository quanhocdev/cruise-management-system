package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.PassengerBookingResponse
import com.project.cruise.android.data.dto.passenger.TripCatalogItem

@Composable
internal fun CatalogOrderDraftForm(item: TripCatalogItem, booking: PassengerBookingResponse) {
    var passengerId by rememberSaveable(booking.id, item.id, item.type) { mutableStateOf<Long?>(null) }
    var quantity by rememberSaveable(booking.id, item.id, item.type) { mutableStateOf("1") }
    var note by rememberSaveable(booking.id, item.id, item.type) { mutableStateOf("") }
    var preview by rememberSaveable(booking.id, item.id, item.type) { mutableStateOf(false) }
    var attempted by rememberSaveable(booking.id, item.id, item.type) { mutableStateOf(false) }
    val error = orderDraftError(item, passengerId, booking.passengers.map { it.passengerVoyageId }.toSet(), quantity)
    val passenger = booking.passengers.firstOrNull { it.passengerVoyageId == passengerId }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Soạn đơn — bản nháp", style = MaterialTheme.typography.titleMedium)
            Text("Chưa có API đặt đơn. Thông tin chỉ nằm trên màn này, chưa gửi cho nhân viên và chưa phát sinh chi phí.")
            if (preview && error == null) {
                Text("Xem lại bản nháp", style = MaterialTheme.typography.titleSmall)
                Text("Hành khách: ${passenger?.fullName}")
                Text("${item.name} × $quantity")
                Text("Tạm tính: ${activityPrice(orderDraftTotal(item.price, quantity))}")
                if (note.isNotBlank()) Text("Ghi chú: $note")
                Text("Giá từ danh mục đồng bộ, chưa phải số tiền được máy chủ xác nhận.")
                OutlinedButton(onClick = { preview = false }) { Text("Sửa bản nháp") }
                Button(onClick = {}, enabled = false) { Text("Gửi đơn — chưa có API") }
            } else {
                Text("Hành khách sử dụng")
                if (booking.passengers.isEmpty()) Text("Booking chưa có hành khách.")
                booking.passengers.forEach { person ->
                    Row(Modifier.fillMaxWidth()) {
                        RadioButton(selected = passengerId == person.passengerVoyageId,
                            onClick = { passengerId = person.passengerVoyageId; preview = false })
                        TextButton(onClick = { passengerId = person.passengerVoyageId; preview = false }) {
                            Text("${person.fullName} · #${person.passengerVoyageId}")
                        }
                    }
                }
                if (item.type == "PRODUCT") {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it.take(3); preview = false },
                        label = { Text("Số lượng (1–99)") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Text("Giới hạn nhập của giao diện, không phải số hàng còn lại.", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("1 lượt cho hành khách đã chọn. Chưa hỗ trợ chọn giờ vì chưa có dữ liệu khung giờ phục vụ.")
                }
                OutlinedTextField(value = note, onValueChange = { note = it.take(300); preview = false },
                    label = { Text("Ghi chú (không bắt buộc)") }, supportingText = { Text("${note.length}/300") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4)
                Text("Tạm tính: ${activityPrice(orderDraftTotal(item.price, quantity))}")
                if (attempted && error != null) Text(error, color = MaterialTheme.colorScheme.error)
                Button(onClick = { attempted = true; preview = error == null }) { Text("Xem lại bản nháp") }
            }
            TextButton(onClick = { passengerId = null; quantity = "1"; note = ""; preview = false; attempted = false }) {
                Text("Xóa nội dung nháp")
            }
            Text("Rời màn có thể mất nội dung nháp. Đây không phải đơn đã đặt.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
