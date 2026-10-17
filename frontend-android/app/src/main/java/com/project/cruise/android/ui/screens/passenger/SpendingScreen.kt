package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.viewmodel.passenger.SpendingViewModel
import com.project.cruise.android.viewmodel.passenger.SpendingState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SpendingScreen(viewModel: SpendingViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    SpendingContent(state, onBack, viewModel::refresh, viewModel::nextPage)
}

@Composable
fun SpendingContent(state: SpendingState, onBack: () -> Unit, onRefresh: () -> Unit, onNextPage: () -> Unit) {
    val money = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")) }
    PassengerPage("Chi tiêu trong chuyến", onBack = onBack) {
        val summary = state.summary
        if (summary != null) {
            item {
                PassengerCard {
                    Text("TỔNG PHÁT SINH", style = MaterialTheme.typography.labelLarge)
                    Text(money.format(summary.totalCharges), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Chi phí sử dụng dịch vụ của booking #${summary.bookingId}; không bao gồm giá đặt tour.", style = MaterialTheme.typography.bodySmall)
                    Text(when(summary.accountStatus) {
                        "OPEN" -> "Đang trong chuyến · số tiền tạm tính"
                        "SETTLING" -> "Đang quyết toán"
                        "CLOSED" -> "Đã chốt tài khoản"
                        else -> "Lịch sử sử dụng dịch vụ"
                    })
                    TextButton(onClick = onRefresh, enabled = !state.loading) { Text("Làm mới") }
                }
            }
            item {
                PassengerCard {
                    val limit = summary.creditLimit
                    if (limit == null) {
                        Text("Hạn mức: Chưa cấu hình", fontWeight = FontWeight.SemiBold)
                        Text("Vui lòng liên hệ bộ phận tài chính trước khi sử dụng dịch vụ có phát sinh phí.")
                    } else {
                        Text("Hạn mức: ${money.format(limit)}", fontWeight = FontWeight.SemiBold)
                        Text("Còn lại: ${money.format((limit - summary.totalCharges).max(BigDecimal.ZERO))}")
                        if (summary.totalCharges >= limit) Text("Đã hết hạn mức cho khoản có phí.")
                    }
                }
            }
            if (summary.items.isEmpty()) item {
                PassengerEmpty("Chưa có phát sinh", "Các lượt sử dụng được nhân viên xác nhận sẽ hiển thị tại đây.")
            }
            items(summary.items, key = { "${it.type}:${it.usageId}" }) { line ->
                PassengerCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(when(line.type) {
                            "PRODUCT" -> "Sản phẩm"
                            "SERVICE" -> "Dịch vụ"
                            "ACTIVITY" -> "Hoạt động trên tàu"
                            "SHORE" -> "Tham quan trên bờ"
                            else -> "Phát sinh"
                        }, fontWeight = FontWeight.SemiBold)
                        Text(money.format(line.finalAmount), fontWeight = FontWeight.Bold)
                    }
                    Text("Hành khách #${line.bookingPassengerId} · Số lượng ${line.quantity}")
                    Text(line.usedAt.replace('T', ' ').take(19), style = MaterialTheme.typography.bodySmall)
                    if (line.discountAmount > BigDecimal.ZERO) Text("Giảm trừ: ${money.format(line.discountAmount)}")
                    if (line.finalAmount.compareTo(BigDecimal.ZERO) == 0) Text("Không phát sinh thêm phí")
                }
            }
            if (summary.items.size < summary.totalItems) item {
                OutlinedButton(onClick = onNextPage, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) { Text("Xem thêm") }
            }
        }
        state.error?.let { message -> item { PassengerError(message, onRefresh) } }
        if (state.loading) item { PassengerLoading() }
        item { Spacer(Modifier.height(12.dp)) }
    }
}
