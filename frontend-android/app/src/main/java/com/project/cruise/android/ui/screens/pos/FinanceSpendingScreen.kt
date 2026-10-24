package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.passengerMoney
import com.project.cruise.android.viewmodel.pos.*

@Composable
fun FinanceSpendingScreen(model: FinanceSpendingViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()
    FinanceSpendingContent(state, onBack, model::find, model::save, model::importHistory)
}

@Composable
fun FinanceSpendingContent(state: FinanceSpendingState, onBack: () -> Unit, onFind: (String) -> Unit,
    onSave: (String,String) -> Unit, onImport: () -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable(state.account?.bookingId) { mutableStateOf("") }
    var reason by rememberSaveable(state.account?.bookingId) { mutableStateOf("") }
    var confirm by remember { mutableStateOf<String?>(null) }
    PosPage {
        PosTopBar("Hạn mức chi tiêu", PosRole.FINANCE, onBack, !state.busy)
        PosPanel {
            Text("Tra cứu booking", style=MaterialTheme.typography.titleLarge)
            Text("Kiểm tra đúng booking trước khi thay đổi hạn mức.")
            OutlinedTextField(code, { code=it }, label={ Text("Mã booking") }, singleLine=true, enabled=!state.busy, modifier=Modifier.fillMaxWidth())
            PosPrimaryButton("Tra cứu", PosRole.FINANCE, { onFind(code) }, !state.busy && code.isNotBlank())
        }
        state.error?.let { PosNotice(it, warning=true) }
        state.message?.let { PosNotice(it) }
        if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.account?.let { account ->
            PosPanel {
                Text(account.bookingCode, style=MaterialTheme.typography.titleLarge)
                Text(account.contactName)
                PosDetailRow("Trạng thái", when(account.status) { "OPEN"->"Đang trong chuyến"; "SETTLING"->"Đang quyết toán"; "CLOSED"->"Đã đóng"; else->"Chưa khởi tạo" })
                PosDetailRow("Đã sử dụng", passengerMoney(account.spent))
                PosDetailRow("Hạn mức hiện tại", account.creditLimit?.let { passengerMoney(it) } ?: "Chưa cấu hình")
            }
            if (account.canEdit) PosPanel {
                Text("Đặt hạn mức mới", style=MaterialTheme.typography.titleMedium)
                OutlinedTextField(amount, { amount=it }, label={ Text("Hạn mức mới (VND)") }, enabled=!state.busy,
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal), singleLine=true, modifier=Modifier.fillMaxWidth())
                Text("Nhập số tiền không có dấu phân cách hàng nghìn. Hạn mức không được thấp hơn số đã sử dụng.")
                OutlinedTextField(reason, { reason=it }, label={ Text("Lý do thay đổi") }, enabled=!state.busy, modifier=Modifier.fillMaxWidth())
                PosPrimaryButton("Lưu hạn mức", PosRole.FINANCE, { confirm="limit" }, !state.busy && SpendingLimitDraft.valid(amount,reason,account.spent))
            } else PosNotice("Không thể đổi hạn mức khi chưa có khách check-in hoặc tài khoản đang quyết toán/đã đóng.")
            if (account.pendingHistoryCount>0) PosPanel {
                Text("${account.pendingHistoryCount} lượt cũ chưa vào sổ chi tiêu")
                Text("Bổ sung lịch sử giữ nguyên số tiền, không thu thêm và không thay đổi trạng thái tài khoản.")
                PosPrimaryButton("Bổ sung lịch sử", PosRole.FINANCE, { confirm="history" }, !state.busy && account.status!="NOT_OPENED")
            }
        }
        Spacer(Modifier.height(12.dp))
    }
    if (confirm!=null && state.account!=null) AlertDialog(onDismissRequest={ confirm=null },
        title={ Text(if(confirm=="limit") "Xác nhận hạn mức" else "Xác nhận bổ sung lịch sử") },
        text={ Text("Booking ${state.account.bookingCode}\n" + if(confirm=="limit") "Hạn mức mới: ${passengerMoney(SpendingLimitDraft.amount(amount))}\nLý do: ${reason.trim()}" else "Bổ sung ${state.account.pendingHistoryCount} lượt cũ. Số tiền không thay đổi.") },
        confirmButton={ TextButton(onClick={ val action=confirm; confirm=null; if(action=="limit") onSave(amount,reason) else onImport() }) { Text("Xác nhận") } },
        dismissButton={ TextButton(onClick={ confirm=null }) { Text("Quay lại") } })
}
