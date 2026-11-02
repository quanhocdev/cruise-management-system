package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.project.cruise.android.data.dto.booking.LedgerEntry
import com.project.cruise.android.ui.components.passengerMoney
import com.project.cruise.android.viewmodel.pos.*

@Composable
fun LedgerCorrectionScreen(model: LedgerCorrectionViewModel, role: PosRole, onBack: () -> Unit) {
    val state by model.state.collectAsState()
    LedgerCorrectionContent(state,role,onBack,model::find,model::correct)
}

@Composable
fun LedgerCorrectionContent(state: LedgerCorrectionState, role: PosRole, onBack: () -> Unit,
    onFind: (String,Int) -> Unit, onCorrect: (Long,String,String,String) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var selected by remember(state.page) { mutableStateOf<LedgerEntry?>(null) }
    var type by remember { mutableStateOf("VOID") }
    var amount by rememberSaveable { mutableStateOf("") }
    var reason by rememberSaveable { mutableStateOf("") }
    var confirming by remember { mutableStateOf(false) }
    PosPage {
        PosTopBar("Điều chỉnh chi tiêu",role,onBack,!state.busy)
        PosNotice(if(role==PosRole.CONVENIENCE) "Chỉ hủy khoản bạn ghi trong 10 phút. Cần có mạng và lý do."
            else "Điều chỉnh khoản chi chưa quyết toán. Màn này không hoàn tiền đã thanh toán qua ngân hàng.")
        PosPanel {
            OutlinedTextField(code,{code=it},label={Text("Mã booking")},singleLine=true,enabled=!state.busy,modifier=Modifier.fillMaxWidth())
            PosPrimaryButton("Tra cứu",role,{onFind(code,0)},!state.busy && code.isNotBlank())
        }
        state.error?.let { PosNotice(it,warning=true) }
        state.receipt?.let { PosNotice(it) }
        if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.page?.let { page ->
            Text("${page.bookingCode} · ${page.totalItems} khoản chi")
            if(page.items.isEmpty()) PosNotice("Không có khoản chi thuộc quyền xử lý của bạn. Lượt cũ chưa vào sổ cần Finance bổ sung lịch sử.")
            page.items.forEach { entry ->
                PosPanel {
                    Text("Khoản #${entry.id} · " + when(entry.sourceType) { "PRODUCT"->"Sản phẩm"; "SERVICE"->"Dịch vụ"; "ACTIVITY"->"Hoạt động trên tàu"; else->"Tham quan" },style=MaterialTheme.typography.titleMedium)
                    Text("Hành khách #${entry.bookingPassengerId}")
                    PosDetailRow("Ban đầu",passengerMoney(entry.amount))
                    PosDetailRow("Đã điều chỉnh",passengerMoney(entry.adjustedAmount))
                    PosDetailRow("Còn lại",passengerMoney(entry.remainingAmount))
                    if(entry.canVoid) OutlinedButton(onClick={selected=entry;type="VOID";amount="";reason="";confirming=false},enabled=!state.busy) { Text("Hủy khoản chi") }
                    if(entry.canRefund) OutlinedButton(onClick={selected=entry;type="REFUND";amount=entry.remainingAmount.toPlainString();reason="";confirming=false},enabled=!state.busy) { Text("Hoàn khoản chi") }
                    if(!entry.canVoid && !entry.canRefund) Text(when(entry.blockedReason) {
                        "HISTORY_RECONCILIATION_REQUIRED" -> "Cần đối soát quyền lợi của lượt cũ."
                        "ENTRY_ALREADY_REVERSED" -> "Đã điều chỉnh hết khoản này."
                        "FINANCE_REQUIRED" -> "Cần Finance xử lý."
                        else -> "Tài khoản chưa mở hoặc đã bắt đầu quyết toán."
                    })
                }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                TextButton(onClick={onFind(page.bookingCode,page.page-1)},enabled=!state.busy && page.page>0) {Text("Trang trước")}
                TextButton(onClick={onFind(page.bookingCode,page.page+1)},enabled=!state.busy && (page.page+1L)*page.size<page.totalItems) {Text("Trang sau")}
            }
        }
    }
    selected?.let { entry ->
        AlertDialog(onDismissRequest={if(!state.busy) {selected=null;confirming=false}}, title={Text(if(confirming) "Xác nhận điều chỉnh" else if(type=="VOID") "Hủy khoản #${entry.id}" else "Hoàn khoản #${entry.id}")},
            text={Column {
                Text("Booking ${state.page?.bookingCode} · Khoản #${entry.id}")
                if(confirming) {
                    Text("Số tiền: ${passengerMoney(if(type=="VOID") entry.remainingAmount else SpendingLimitDraft.amount(amount))}")
                    Text("Lý do: ${reason.trim()}")
                    state.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
                } else {
                    if(type=="REFUND") OutlinedTextField(amount,{amount=it},label={Text("Số tiền hoàn (VND)")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
                    OutlinedTextField(reason,{reason=it},label={Text("Lý do điều chỉnh")})
                    Text("Lượt trong gói được trả lại khi điều chỉnh hết khoản. Không hủy việc tham gia hoặc trả hàng tồn kho.")
                }
            }},
            confirmButton={TextButton(enabled=!state.busy && CorrectionDraft.valid(entry,type,amount,reason),onClick={
                if(!confirming) confirming=true else onCorrect(entry.id,type,amount,reason)
            }) {Text(if(state.busy) "Đang gửi…" else if(confirming) "Xác nhận" else "Tiếp tục")}},
            dismissButton={TextButton(enabled=!state.busy,onClick={selected=null;confirming=false}) {Text("Quay lại")}})
    }
}
