package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.pos.FinanceReportViewModel
import java.math.BigDecimal

@Composable
fun FinanceReportScreen(model:FinanceReportViewModel,onBill:(Long)->Unit,onBack:()->Unit) {
    val state by model.state.collectAsState()
    var choose by rememberSaveable{mutableStateOf(false)}
    var search by rememberSaveable{mutableStateOf("")}
    LaunchedEffect(Unit){model.loadTours()}
    PassengerPage("Thống kê chuyến",onBack=onBack) {
        item{OceanBanner("TỔNG QUAN FINANCE",state.selected?.name?:"Theo dõi chi tiêu & thu tiền",
            "Bill dịch vụ trong chuyến. Tiền vé đặt tour được theo dõi riêng.")}
        item{OutlinedButton(onClick={choose=true},enabled=!state.tourLoading,modifier=Modifier.fillMaxWidth()){
            Text(if(state.selected==null)"Chọn chuyến" else "Đổi chuyến")}}
        if(state.tourLoading)item{PassengerLoading()}
        state.tourError?.let{item{PassengerError(it){model.loadTours()}}}
        if(!state.tourLoading && state.tourError==null && state.tours.isEmpty())item{PassengerEmpty("Chưa có chuyến","Chưa có chuyến sẵn sàng, đang chạy hoặc đã hoàn tất trong danh mục.")}
        if(state.busy)item{PassengerLoading()}
        state.error?.let{item{PassengerError(it){model.refresh()}}}
        state.report?.let{report->
            item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                Text("${report.charges.bookings} booking · ${report.settlement.billCount} bill",style=MaterialTheme.typography.labelLarge)
                TextButton(onClick=model::refresh){Text("Làm mới")}
            }}
            item{PassengerCard{
                Text("Phát sinh dịch vụ",style=MaterialTheme.typography.titleMedium)
                Text(passengerMoney(report.charges.net),style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.primary)
                ReportMoneyRow("Trước điều chỉnh",report.charges.gross)
                ReportMoneyRow("Đã hủy khoản chi",report.charges.voided)
                ReportMoneyRow("Đã hoàn vào sổ chi tiêu",report.charges.refunded)
                Text("Đã tính các khoản ngoài gói sau quyền lợi. Giảm trừ trong sổ không đồng nghĩa tiền đã hoàn qua ngân hàng.",style=MaterialTheme.typography.bodySmall)
            }}
            item{PassengerCard{
                Text("Thanh toán bill dịch vụ",style=MaterialTheme.typography.titleMedium)
                ReportMoneyRow("Tổng bill đã chốt",report.settlement.billed)
                if(!report.settlement.paymentAvailable)Text("Chưa lấy được dữ liệu thanh toán. Chưa xác định tiền đã thu và còn phải thu.",color=MaterialTheme.colorScheme.error)
                else {
                    ReportMoneyRow("Đã thu qua thanh toán",report.settlement.collected)
                    ReportMoneyRow("Đã hoàn tiền thành công",report.settlement.returned)
                    ReportMoneyRow("Tiền thu sau hoàn",report.settlement.netCollected)
                    ReportMoneyRow("Hoàn tiền đang xử lý",report.settlement.refundPending)
                    HorizontalDivider()
                    ReportMoneyRow("Còn phải thu trên bill chưa trả",report.settlement.unpaidBillAmount)
                }
                Text("${report.settlement.unsettledBillCount} bill chưa xác nhận trả xong",style=MaterialTheme.typography.bodySmall)
                Text("Số còn phải thu chỉ gồm bill đã chốt; không gồm chi tiêu chưa chốt bill. Bill 0 đồng không phát sinh tiền thu.",style=MaterialTheme.typography.bodySmall)
                if(report.settlement.reviewCount>0)Text("${report.settlement.reviewCount} bill cần đối soát trạng thái hoặc giao dịch.",color=MaterialTheme.colorScheme.error)
                if(report.settlement.unverifiedPaidBills>0)Text("${report.settlement.unverifiedPaidBills} bill đánh dấu đã trả nhưng chưa khớp tiền thu.",color=MaterialTheme.colorScheme.error)
            }}
            item{PassengerSection("Danh sách bill","Bộ lọc dưới đây chỉ áp dụng danh sách; tổng phía trên luôn là toàn chuyến.")}
            item{Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                listOf("ALL" to "Tất cả","UNPAID" to "Chưa trả","PAID" to "Đã trả").forEach{(value,label)->
                    FilterChip(selected=state.status==value,onClick={model.filter(value)},label={Text(label)})}
            }}
            item{TextButton(onClick={model.sort(if(state.sort=="newest")"amountDesc" else "newest")}){
                Text(if(state.sort=="newest")"Sắp xếp: mới nhất" else "Sắp xếp: tiền cao nhất")}}
            if(report.bills.items.isEmpty())item{PassengerEmpty("Chưa có bill phù hợp","Thử đổi bộ lọc hoặc chọn chuyến khác.")}
            items(report.bills.items,key={it.billId}){bill->PassengerCard{
                Text(bill.bookingCode,style=MaterialTheme.typography.titleMedium)
                Text(bill.billCode,style=MaterialTheme.typography.bodySmall)
                Text(if(bill.status=="PAID")"Đã quyết toán" else if(bill.status=="PAYMENT_FAILED")"Thanh toán chưa thành công" else "Chờ thanh toán")
                Text(passengerMoney(bill.amount),style=MaterialTheme.typography.titleLarge)
                if(bill.needsReview)Text("Cần đối soát",color=MaterialTheme.colorScheme.error)
                OutlinedButton(onClick={onBill(bill.bookingId)},modifier=Modifier.fillMaxWidth()){Text("Xem bill ${bill.bookingCode}")}
            }}
            item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                TextButton(onClick={model.page(state.page-1)},enabled=state.page>0){Text("Trang trước")}
                Text("Trang ${report.bills.page+1} · ${report.bills.totalItems} bill")
                TextButton(onClick={model.page(state.page+1)},enabled=(report.bills.page.toLong()+1)*report.bills.size<report.bills.totalItems){Text("Trang sau")}
            }}
            item{Text("Số liệu có thể thay đổi khi giao dịch được xác nhận. Bấm Làm mới để cập nhật.",style=MaterialTheme.typography.bodySmall)}
        }
    }
    if(choose)AlertDialog(onDismissRequest={choose=false},title={Text("Chọn chuyến")},text={Column{
        OutlinedTextField(value=search,onValueChange={search=it},label={Text("Tên hoặc mã chuyến")},singleLine=true)
        LazyColumn(Modifier.heightIn(max=360.dp)){
            items(state.tours.filter{it.name.contains(search,true)||it.code.orEmpty().contains(search,true)},key={it.id}){tour->
                TextButton(onClick={choose=false;model.select(tour)},modifier=Modifier.fillMaxWidth()){
                    Column(Modifier.fillMaxWidth()){Text(tour.name);Text("${tour.code.orEmpty()} · ${passengerDate(tour.startDate)}",style=MaterialTheme.typography.bodySmall)}
                }
            }
        }
    }},confirmButton={TextButton(onClick={choose=false}){Text("Đóng")}})
}

@Composable
private fun ReportMoneyRow(label:String,amount:BigDecimal?) {
    Column(Modifier.fillMaxWidth().padding(vertical=3.dp)){
        Text(label,style=MaterialTheme.typography.bodySmall)
        Text(amount?.let(::passengerMoney)?:"Chưa xác định",style=MaterialTheme.typography.titleMedium)
    }
}
