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
fun PaymentRefundScreen(model: PaymentRefundViewModel,onBack: () -> Unit) {
    val state by model.state.collectAsState()
    PaymentRefundContent(state,onBack,model::find,model::select,model::refund)
}

@Composable
fun PaymentRefundContent(state: PaymentRefundState,onBack: () -> Unit,onFind: (String)->Unit,
    onSelect: (Long,Int)->Unit,onRefund: (String,String)->Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable(state.summary?.paymentId) { mutableStateOf("") }
    var reason by rememberSaveable(state.summary?.paymentId) { mutableStateOf("") }
    var confirm by remember { mutableStateOf(false) }
    LaunchedEffect(state.receipt) { if(state.receipt!=null) { confirm=false; amount=""; reason="" } }
    PosPage {
        PosTopBar("Hoàn tiền thanh toán",PosRole.FINANCE,onBack,!state.busy)
        PosPanel {
            Text("Thanh toán vé",style=MaterialTheme.typography.titleLarge)
            Text("Tra cứu booking để hoàn một phần hoặc toàn bộ tiền vé đã thanh toán qua VNPay.")
            Text("Hoàn tiền không tự hủy booking. Kiểm tra việc hủy vé trước khi gửi yêu cầu.")
            OutlinedTextField(code,{code=it},label={Text("Mã booking")},enabled=!state.busy,singleLine=true,modifier=Modifier.fillMaxWidth())
            PosPrimaryButton("Tra cứu thanh toán",PosRole.FINANCE,{onFind(code)},!state.busy && code.isNotBlank())
        }
        if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { PosNotice(it,warning=true) }
        state.receipt?.let { PosNotice(it) }
        if(state.bookingCode.isNotBlank() && !state.busy && state.error==null && state.payments.isEmpty()) PosNotice("Chưa có giao dịch thanh toán cho booking này.")
        state.payments.forEach { p -> PosPanel {
            Text("Giao dịch #${p.id} · ${state.bookingCode}",style=MaterialTheme.typography.titleMedium)
            Text("${passengerMoney(p.amount)} · ${p.method}")
            Text(when(p.status){ "SUCCESS"->"Đã thanh toán"; "REFUNDED"->"Đã hoàn toàn bộ"; "PENDING"->"Chờ thanh toán"; "FAILED"->"Thanh toán thất bại"; "EXPIRED"->"Hết hạn thanh toán"; else->p.status })
            OutlinedButton(onClick={onSelect(p.id,0)},enabled=!state.busy) { Text("Xem khoản hoàn") }
        } }
        state.summary?.let { s ->
            PosPanel {
                Text("Giao dịch #${s.paymentId}",style=MaterialTheme.typography.titleLarge)
                Text("Đã hoàn: ${passengerMoney(s.refundedAmount)}")
                Text("Đang chờ: ${passengerMoney(s.pendingAmount)}")
                Text("Còn có thể hoàn: ${passengerMoney(s.refundableAmount)}")
                OutlinedButton(onClick={onSelect(s.paymentId,s.page)},enabled=!state.busy) { Text("Cập nhật kết quả") }
            }
            if(s.canRefund) PosPanel {
                OutlinedTextField(amount,{amount=it},label={Text("Số tiền hoàn (VND)")},enabled=!state.busy,singleLine=true,
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
                OutlinedTextField(reason,{reason=it},label={Text("Lý do hoàn tiền")},enabled=!state.busy,modifier=Modifier.fillMaxWidth())
                PosPrimaryButton("Yêu cầu hoàn tiền",PosRole.FINANCE,{confirm=true},!state.busy && PaymentRefundDraft.valid(s,amount,reason))
            } else PosNotice(if(s.pendingAmount.signum()>0) "Đang có yêu cầu chờ xác minh. Không gửi thêm khoản hoàn." else "Giao dịch này hiện không thể tạo khoản hoàn mới.")
            s.refunds.forEach { r -> PosPanel {
                Text(PaymentRefundDraft.status(r.status),style=MaterialTheme.typography.titleMedium)
                Text(passengerMoney(r.amount)); Text(r.reason); Text("Mã yêu cầu: ${r.id.take(8)}")
            } }
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick={onSelect(s.paymentId,s.page-1)},enabled=!state.busy && s.page>0) { Text("Trước") }
                OutlinedButton(onClick={onSelect(s.paymentId,s.page+1)},enabled=!state.busy && s.refunds.size==s.size) { Text("Tiếp") }
            }
        }
    }
    if(confirm && state.summary!=null) AlertDialog(onDismissRequest={if(!state.busy)confirm=false},
        title={Text("Xác nhận hoàn tiền")},
        text={Text("Booking ${state.bookingCode} · giao dịch #${state.summary.paymentId}\n${passengerMoney(SpendingLimitDraft.amount(amount))}\nLý do: ${reason.trim()}\n${state.error.orEmpty()}")},
        confirmButton={TextButton(onClick={onRefund(amount,reason)},enabled=!state.busy){Text(if(state.busy)"Đang gửi…" else "Xác nhận gửi VNPay")}},
        dismissButton={TextButton(onClick={confirm=false},enabled=!state.busy){Text("Quay lại")}})
}
