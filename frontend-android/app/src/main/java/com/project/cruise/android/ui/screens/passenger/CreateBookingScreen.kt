package com.project.cruise.android.ui.screens.passenger

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.BookingDraft
import com.project.cruise.android.viewmodel.passenger.PassengerBookingState
import com.project.cruise.android.viewmodel.passenger.PassengerDraft
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CreateBookingScreen(
    state: PassengerBookingState,
    onDraftChange: (BookingDraft) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    BackHandler(enabled = state.submitting) {}
    val booking = state.booking
    if (booking != null) {
        OceanPage {
            OceanHeader("Đặt chỗ thành công")
            OceanBanner(
                "ĐẶT CHỖ CỦA BẠN",
                booking.bookingCode ?: "Booking #${booking.id}",
                bookingLabel(booking.status),
            )
            OceanStatePanel(
                "Tạo booking thành công",
                "Tổng giá trị: ${passengerMoney(booking.totalAmount)}. Xem thông tin và trạng thái đặt chỗ trong Chuyến đi của tôi.",
                "✓",
                "Xem chuyến đi của tôi",
                onDone,
            )
        }
        return
    }

    val draft = state.draft
    val editable = !state.submitting && !state.outcomeUnknown
    val pkg = state.tourPackage
    val capacity = (pkg?.maxPassengers ?: 2).coerceAtLeast(1) * draft.numberOfRooms

    OceanPage {
        OceanHeader("Đặt chỗ cho hành trình", onBack, !state.submitting)
        OceanBanner(
            "KỲ NGHỈ SẮP TỚI",
            "Chuẩn bị cho\nhành trình của bạn",
            "Chỉ cần thông tin liên hệ và giấy tờ hành khách.",
        )
        PassengerPill("01 Liên hệ  ·  02 Phòng  ·  03 Hành khách")

        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        pkg?.let {
            PassengerCard {
                Text("GÓI ĐÃ CHỌN", style = MaterialTheme.typography.labelSmall, color = OceanTeal)
                Text(
                    it.name ?: "Gói tour",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(it.description.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    passengerMoney(it.price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
                Text(
                    "Giá mỗi phòng",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate,
                )
                Text("Sức chứa: ${it.maxPassengers ?: 2} khách/phòng")
            }
        }

        BookingSection("01 · Người liên hệ") {
            BookingField("Họ và tên *", draft.contactName, editable) {
                onDraftChange(draft.copy(contactName = it))
            }
            BookingField("Số điện thoại *", draft.contactPhone, editable, KeyboardType.Phone) {
                onDraftChange(draft.copy(contactPhone = it))
            }
        }

        BookingSection("02 · Chọn số phòng") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        onDraftChange(
                            draft.copy(numberOfRooms = (draft.numberOfRooms - 1).coerceAtLeast(1))
                        )
                    },
                    enabled = editable && draft.numberOfRooms > 1,
                ) {
                    Text("−")
                }
                Text(
                    "${draft.numberOfRooms} phòng",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedButton(
                    onClick = {
                        onDraftChange(
                            draft.copy(numberOfRooms = (draft.numberOfRooms + 1).coerceAtMost(20))
                        )
                    },
                    enabled = editable && draft.numberOfRooms < 20,
                ) {
                    Text("+")
                }
            }
            Text("Tối đa $capacity hành khách", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        draft.passengers.forEachIndexed { index, passenger ->
            BookingSection("03 · Hành khách ${index + 1}") {
                PassengerFields(passenger, editable) { updated ->
                    onDraftChange(
                        draft.copy(
                            passengers =
                                draft.passengers.mapIndexed { i, old ->
                                    if (i == index) updated else old
                                }
                        )
                    )
                }
                if (draft.passengers.size > 1) {
                    TextButton(
                        onClick = {
                            onDraftChange(
                                draft.copy(
                                    passengers =
                                        draft.passengers.filterIndexed { i, _ -> i != index }
                                )
                            )
                        },
                        enabled = editable,
                    ) {
                        Text("Xóa hành khách này", color = OceanCoral)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                onDraftChange(draft.copy(passengers = draft.passengers + PassengerDraft()))
            },
            enabled = editable && draft.passengers.size < minOf(20, capacity),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Thêm hành khách")
        }

        state.error?.let {
            OceanStatePanel(
                if (state.outcomeUnknown) "Cần kiểm tra booking" else "Chưa thể đặt tour",
                it,
                "!",
                if (state.outcomeUnknown) "Mở Booking của tôi" else null,
                if (state.outcomeUnknown) onDone else null,
                true,
            )
        }

        if (pkg == null && !state.loading && !state.outcomeUnknown) {
            OceanStatePanel(
                "Không tải được gói tour",
                "Kiểm tra kết nối rồi thử lại.",
                "↻",
                "Thử lại",
                onRetry,
                true,
            )
        }

        if (pkg != null)
            PassengerCard {
                PassengerSection("Kiểm tra đặt chỗ")
                Text(
                    "${draft.numberOfRooms} phòng · ${draft.passengers.size} hành khách",
                    color = OceanSlate,
                )
                HorizontalDivider(color = OceanLine)
                Text(
                    "Tạm tính theo số phòng",
                    style = MaterialTheme.typography.labelMedium,
                    color = OceanSlate,
                )
                Text(
                    passengerMoney(pkg.price?.multiply(java.math.BigDecimal(draft.numberOfRooms))),
                    style = MaterialTheme.typography.headlineSmall,
                    color = OceanTeal,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Tổng giá trị chính thức được xác định khi tạo đặt chỗ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate,
                )
            }

        OceanPrimaryButton(
            text = if (state.submitting) "Đang tạo booking…" else "Xác nhận đặt tour",
            onClick = onSubmit,
            enabled = editable && !state.loading && pkg != null,
            loading = state.submitting,
        )
        Text(
            "Booking được tạo ở trạng thái chờ thanh toán. Giá cuối cùng do máy chủ tính theo số phòng.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    PassengerCard {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun PassengerFields(
    passenger: PassengerDraft,
    enabled: Boolean,
    onChange: (PassengerDraft) -> Unit,
) {
    val context = LocalContext.current
    BookingField("Họ và tên *", passenger.fullName, enabled) {
        onChange(passenger.copy(fullName = it))
    }
    OutlinedButton(
        onClick = {
            val initial =
                runCatching { LocalDate.parse(passenger.dateOfBirth) }
                    .getOrDefault(LocalDate.now().minusYears(18))
            DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onChange(
                            passenger.copy(
                                dateOfBirth = LocalDate.of(year, month + 1, day).toString()
                            )
                        )
                    },
                    initial.year,
                    initial.monthValue - 1,
                    initial.dayOfMonth,
                )
                .apply {
                    datePicker.maxDate =
                        LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli() - 1
                }
                .show()
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            if (passenger.dateOfBirth.isBlank()) "Chọn ngày sinh *"
            else "Ngày sinh: ${passengerDate(passenger.dateOfBirth)}"
        )
    }

    Text("Giới tính *", fontWeight = FontWeight.SemiBold)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf("MALE" to "Nam", "FEMALE" to "Nữ", "OTHER" to "Khác").forEach { (value, label) ->
            FilterChip(
                selected = passenger.gender == value,
                onClick = { onChange(passenger.copy(gender = value)) },
                enabled = enabled,
                label = { Text(label) },
            )
        }
    }

    Text("Loại giấy tờ *", fontWeight = FontWeight.SemiBold)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(
                "CCCD" to "CCCD",
                "PASSPORT" to "Hộ chiếu",
                "BIRTH_CERTIFICATE" to "Khai sinh",
                "OTHER" to "Khác",
            )
            .forEach { (value, label) ->
                FilterChip(
                    selected = passenger.idCardType == value,
                    onClick = { onChange(passenger.copy(idCardType = value)) },
                    enabled = enabled,
                    label = { Text(label) },
                )
            }
    }
    BookingField("Số giấy tờ *", passenger.identificationNumber, enabled) {
        onChange(passenger.copy(identificationNumber = it))
    }
    BookingField("Số điện thoại", passenger.phone, enabled, KeyboardType.Phone) {
        onChange(passenger.copy(phone = it))
    }
    BookingField("Email", passenger.email, enabled, KeyboardType.Email) {
        onChange(passenger.copy(email = it))
    }
    BookingField("Ghi chú giấy tờ", passenger.documentNote, enabled) {
        onChange(passenger.copy(documentNote = it))
    }
}

@Composable
private fun BookingField(
    label: String,
    value: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = OceanLine,
                focusedBorderColor = OceanTeal,
            ),
        modifier = Modifier.fillMaxWidth(),
    )
}
