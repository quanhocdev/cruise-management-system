package com.project.cruise.android.ui.screens.passenger

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.BookingDraft
import com.project.cruise.android.viewmodel.passenger.PassengerBookingState
import com.project.cruise.android.viewmodel.passenger.PassengerDraft
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun CreateBookingScreen(
    state: PassengerBookingState,
    onDraftChange: (BookingDraft) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    BackHandler(enabled = state.submitting) { /* Keep the pending request on screen. */ }
    state.booking?.let { booking ->
        BookingCreatedScreen(booking, onDone)
        return
    }
    val draft = state.draft
    val editable = !state.submitting && !state.outcomeUnknown
    val room = state.room
    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().imePadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TextButton(onClick = onBack, enabled = !state.submitting) { Text("← Chọn phòng khác") }
            Text("Thông tin đặt phòng", style = MaterialTheme.typography.headlineMedium)
        }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        if (room != null) item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("${room.roomTypeName} · ${room.roomCode}", style = MaterialTheme.typography.titleLarge)
                    Text("Tầng ${room.deckNumber} · Còn ${room.remainingCapacity} chỗ")
                    Text("Giá tham khảo: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(room.price)}")
                    Text("Giá chính thức và số chỗ được backend kiểm tra lúc xác nhận.")
                }
            }
        }
        item {
            Text("Người liên hệ", style = MaterialTheme.typography.titleLarge)
            BookingField("Họ tên *", draft.contactName, editable) { onDraftChange(draft.copy(contactName = it)) }
            BookingField("Số điện thoại *", draft.contactPhone, editable, KeyboardType.Phone) {
                onDraftChange(draft.copy(contactPhone = it))
            }
        }
        itemsIndexed(draft.passengers) { index, passenger ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Hành khách ${index + 1}", style = MaterialTheme.typography.titleMedium)
                    PassengerFields(passenger, editable) { updated ->
                        onDraftChange(draft.copy(passengers = draft.passengers.mapIndexed { i, old -> if (i == index) updated else old }))
                    }
                    if (draft.passengers.size > 1) TextButton(
                        onClick = { onDraftChange(draft.copy(passengers = draft.passengers.filterIndexed { i, _ -> i != index })) },
                        enabled = editable
                    ) { Text("Xóa hành khách này") }
                }
            }
        }
        item {
            OutlinedButton(
                onClick = { onDraftChange(draft.copy(passengers = draft.passengers + PassengerDraft())) },
                enabled = editable && !state.loading && room?.available == true &&
                    draft.passengers.size < minOf(20L, room.remainingCapacity),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Thêm hành khách") }
        }
        state.error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
        if (room == null && !state.loading && !state.outcomeUnknown) item {
            OutlinedButton(onClick = onRetry, enabled = editable) { Text("Tải lại phòng") }
        }
        item {
            Button(
                onClick = onSubmit,
                enabled = editable && !state.loading && room?.available == true && room.remainingCapacity > 0,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.submitting) "Đang giữ chỗ…" else "Xác nhận giữ chỗ") }
            Text("Bước này tạo booking, chưa thanh toán hoặc trừ tiền.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PassengerFields(passenger: PassengerDraft, enabled: Boolean, onChange: (PassengerDraft) -> Unit) {
    val context = LocalContext.current
    BookingField("Họ tên *", passenger.fullName, enabled) { onChange(passenger.copy(fullName = it)) }
    OutlinedButton(onClick = {
        val initial = runCatching { LocalDate.parse(passenger.dateOfBirth) }.getOrDefault(LocalDate.now().minusYears(18))
        DatePickerDialog(context, { _, year, month, day ->
            onChange(passenger.copy(dateOfBirth = LocalDate.of(year, month + 1, day).toString()))
        }, initial.year, initial.monthValue - 1, initial.dayOfMonth).apply {
            datePicker.maxDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        }.show()
    }, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
        Text(if (passenger.dateOfBirth.isEmpty()) "Chọn ngày sinh *" else "Ngày sinh: ${passenger.dateOfBirth}")
    }
    Text("Giới tính *")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("MALE" to "Nam", "FEMALE" to "Nữ", "OTHER" to "Khác").forEach { (value, label) ->
            FilterChip(selected = passenger.gender == value, onClick = { onChange(passenger.copy(gender = value)) },
                enabled = enabled, label = { Text(label) })
        }
    }
    BookingField("Số điện thoại (không bắt buộc)", passenger.phone, enabled, KeyboardType.Phone) {
        onChange(passenger.copy(phone = it))
    }
    BookingField("Email (không bắt buộc)", passenger.email, enabled, KeyboardType.Email) {
        onChange(passenger.copy(email = it))
    }
}

@Composable
private fun BookingField(
    label: String, value: String, enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit
) {
    OutlinedTextField(value, onChange, label = { Text(label) }, enabled = enabled,
        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
}
