package com.project.cruise.android.ui.passenger.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.project.cruise.android.ui.passenger.components.booking.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.BookingDraft
import com.project.cruise.android.viewmodel.passenger.PassengerBookingState
import com.project.cruise.android.viewmodel.passenger.PassengerDraft
import java.math.BigDecimal

@Composable
fun BookingScreen(
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
        BookingSuccessContent(booking = booking, onDone = onDone)
        return
    }

    val draft = state.draft
    val editable = !state.submitting && !state.outcomeUnknown
    val pkg = state.tourPackage
    val capacityPerRoom = pkg?.maxPassengers?.takeIf { it > 0 }
    val capacity = (capacityPerRoom?.toLong() ?: 0L) * draft.numberOfRooms

    OceanPage {
        OceanHeader("Đặt chỗ cho hành trình", onBack, !state.submitting)
        OceanBanner(
            "KỲ NGHỈ SẮP TỚI",
            "Chuẩn bị cho\nhành trình của bạn",
            "Chỉ cần thông tin liên hệ và giấy tờ hành khách.",
        )
        BookingStepIndicator()

        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth(), color = OceanTeal)
        }

        pkg?.let {
            SelectedPackageCard(
                name = it.name,
                description = it.description,
                price = it.price,
                capacityPerRoom = capacityPerRoom,
            )
        }

        BookingSection(step = "01", title = "Người liên hệ") {
            BookingTextField("Họ và tên *", draft.contactName, editable) {
                onDraftChange(draft.copy(contactName = it))
            }
            BookingTextField("Số điện thoại *", draft.contactPhone, editable, KeyboardType.Phone) {
                onDraftChange(draft.copy(contactPhone = it))
            }
        }

        BookingSection(step = "02", title = "Chọn số phòng") {
            RoomCounter(
                rooms = draft.numberOfRooms,
                enabled = editable,
                capacityPerRoom = capacityPerRoom,
                totalCapacity = capacity,
                onChange = { onDraftChange(draft.copy(numberOfRooms = it)) },
            )
        }

        draft.passengers.forEachIndexed { index, passenger ->
            PassengerFormSection(
                index = index,
                passenger = passenger,
                enabled = editable,
                canRemove = draft.passengers.size > 1,
                onChange = { updated ->
                    onDraftChange(
                        draft.copy(
                            passengers =
                                draft.passengers.mapIndexed { i, old ->
                                    if (i == index) updated else old
                                }
                        )
                    )
                },
                onRemove = {
                    onDraftChange(
                        draft.copy(
                            passengers = draft.passengers.filterIndexed { i, _ -> i != index }
                        )
                    )
                },
            )
        }

        AddPassengerButton(
            enabled = editable && draft.passengers.size < minOf(20L, capacity),
            onClick = {
                onDraftChange(draft.copy(passengers = draft.passengers + PassengerDraft()))
            },
        )

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

        if ((pkg == null || capacityPerRoom == null) && !state.loading && !state.outcomeUnknown) {
            OceanStatePanel(
                if (pkg == null) "Không tải được gói tour" else "Gói tour chưa có sức chứa hợp lệ",
                "Tải lại thông tin hoặc chọn gói tour khác.",
                "↻",
                "Thử lại",
                onRetry,
                true,
            )
        }

        if (pkg != null)
            BookingReviewCard(
                roomCount = draft.numberOfRooms,
                passengerCount = draft.passengers.size,
                estimatedTotal = pkg.price?.multiply(BigDecimal(draft.numberOfRooms)),
            )

        OceanPrimaryButton(
            text = if (state.submitting) "Đang tạo booking…" else "Xác nhận đặt tour",
            onClick = onSubmit,
            enabled = editable && !state.loading && pkg != null && capacityPerRoom != null,
            loading = state.submitting,
        )
        Text(
            "Booking được tạo ở trạng thái chờ thanh toán. Giá cuối cùng do máy chủ tính theo số phòng.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}