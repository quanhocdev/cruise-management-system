package com.project.cruise.android.ui.screens.pos

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.pos.*
import kotlinx.coroutines.*

@Composable
fun CheckoutScreen(model: CheckoutViewModel,finance: Boolean,bookingId: Long?=null,onBack: ()->Unit){
    val state by model.state.collectAsState()
    val context=LocalContext.current
    var code by rememberSaveable{mutableStateOf("")}
    var confirm by rememberSaveable{mutableStateOf(false)}
    var launchError by remember{mutableStateOf<String?>(null)}
    val owner=LocalLifecycleOwner.current
    LaunchedEffect(bookingId){bookingId?.let(model::open)}
    DisposableEffect(owner){
        val observer=LifecycleEventObserver{_,event->if(event==Lifecycle.Event.ON_RESUME)model.refresh()}
        owner.lifecycle.addObserver(observer)
        onDispose{owner.lifecycle.removeObserver(observer)}
    }
    LaunchedEffect(state.bill?.paymentState){
        if(state.bill?.paymentState in setOf("PENDING","VERIFYING")){
            while(true){delay(15000);model.refresh()}
        }
    }
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")){uri->
        if(uri!=null)model.export{bytes->withContext(Dispatchers.IO){requireNotNull(context.contentResolver.openOutputStream(uri)).use{it.write(bytes)}}}
    }
    PassengerPage(if(finance)"Quyết toán cuối chuyến" else "Bill dịch vụ",onBack=onBack){
        item{OceanBanner("CHI TIÊU TRONG CHUYẾN",state.bill?.bookingCode?:"Bill & biên nhận",
            "Tổng các dịch vụ đã sử dụng và khoản điều chỉnh. Tiền vé đặt tour được theo dõi riêng.")}
        if(finance)item{PassengerCard{
            OutlinedTextField(value=code,onValueChange={code=it},label={Text("Mã booking")},singleLine=true,enabled=!state.busy,modifier=Modifier.fillMaxWidth())
            Button(onClick={model.find(code)},enabled=!state.busy && code.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Tra cứu bill")}
        }}
        if(state.busy)item{PassengerLoading()}
        state.error?.let{item{PassengerError(it){model.refresh()}}}
        state.message?.let{item{Text(it)}}
        launchError?.let{item{Text(it)}}
        state.bill?.let{bill->
            item{PassengerCard{
                Text(CheckoutDisplay.title(bill.paymentState),style=MaterialTheme.typography.titleLarge)
                Text(bill.billCode?:"Bill chưa được Finance xác nhận")
                Text(when(bill.accountStatus){"CLOSED"->"Tài khoản chi tiêu đã đóng";"SETTLING"->"Tài khoản đang quyết toán";else->"Tài khoản chưa chốt"})
                bill.totalAmount?.let{Text(passengerMoney(it),style=MaterialTheme.typography.headlineMedium)}
                if(bill.billStatus=="PAID" && bill.totalAmount?.signum()==0)Text("Không phát sinh thanh toán.")
                if(bill.paymentState in setOf("UNKNOWN","VERIFYING","UNAVAILABLE"))Text("Đang chờ kết quả được xác minh. Không tạo thêm giao dịch hoặc coi việc quay lại app là đã thanh toán.")
                OutlinedButton(onClick=model::refresh,enabled=!state.busy){Text("Cập nhật trạng thái")}
            }}
            if(finance && CheckoutDisplay.canPay(bill))item{Button(onClick=model::pay,enabled=!state.busy,modifier=Modifier.fillMaxWidth()){Text(if(bill.paymentState=="FAILED")"Thử thanh toán lại" else "Tạo thanh toán VNPay")}}
            if(finance)CheckoutDisplay.paymentUrl(bill)?.let{url->item{
                Button(onClick={try{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}catch(e:Exception){launchError="Không mở được trình duyệt. Vui lòng thử lại."}},
                    enabled=!state.busy,modifier=Modifier.fillMaxWidth()){Text("Mở trang VNPay")}
            }}
            if(bill.billStatus=="PAID")item{OutlinedButton(onClick={export.launch("bill-dich-vu-${bill.billId}.pdf")},enabled=!state.busy,modifier=Modifier.fillMaxWidth()){Text("Lưu biên nhận PDF")}}
            if(bill.items.isNotEmpty())item{PassengerSection("Chi tiết đã chốt","Đơn giá và khoản giảm trừ được lưu tại thời điểm chốt bill.")}
            bill.items.forEach{line->item{PassengerCard{
                Text(line.passengerName?:"Hành khách #${line.bookingPassengerId}",style=MaterialTheme.typography.titleMedium)
                Text("${CheckoutDisplay.kind(line.usageType)} · lượt #${line.usageId}")
                Text("${line.quantity} × ${passengerMoney(line.unitPrice)}")
                if(line.discountAmount.signum()!=0)Text("Giảm trừ: ${passengerMoney(line.discountAmount)}")
                Text(passengerMoney(line.finalAmount),style=MaterialTheme.typography.titleMedium)
            }}}
            if(bill.billId==null && !finance)item{Text("Finance chưa chốt bill cho booking này. Bạn có thể xem chi tiêu trong chuyến ở màn chi tiết booking.")}
        }
        state.preview?.let{preview->
            item{PassengerSection("Kiểm tra trước khi chốt","Sau xác nhận, các khoản chi của booking sẽ được khóa để quyết toán.")}
            preview.passengers.forEach{guest->item{PassengerCard{
                Text(guest.passengerName?:"Hành khách #${guest.bookingPassengerId}",style=MaterialTheme.typography.titleMedium)
                listOf("Tham quan bờ" to guest.activityVisitUsages,"Hoạt động trên tàu" to guest.activityCruiseUsages,
                    "Dịch vụ" to guest.serviceUsages,"Sản phẩm" to guest.productUsages).forEach{(kind,usages)->
                    usages.forEach{line->
                        Text("$kind #${line.usageId} · ${line.quantity?:1} lượt · ${passengerMoney(line.finalAmount)}")
                        if(line.adjustmentAmount?.signum()!=null && line.adjustmentAmount.signum()!=0)
                            Text("Đã điều chỉnh: ${passengerMoney(line.adjustmentAmount)}",style=MaterialTheme.typography.bodySmall)
                    }
                }
                Text("Tổng: ${passengerMoney(guest.passengerTotal)}",style=MaterialTheme.typography.titleMedium)
            }}}
            item{PassengerCard{Text("Tổng quyết toán",style=MaterialTheme.typography.titleMedium);Text(passengerMoney(preview.grandTotal),style=MaterialTheme.typography.headlineMedium)}}
            if(finance)item{Button(onClick={confirm=true},enabled=!state.busy && state.error==null && preview.grandTotal.signum()>=0,modifier=Modifier.fillMaxWidth()){Text("Kiểm tra & chốt bill")}}
        }
        item{Text("Trạng thái bill phản ánh quyết toán chi tiêu. Việc trả phòng và thu hồi vòng tay được nhân viên xử lý riêng.",style=MaterialTheme.typography.bodySmall)}
    }
    if(confirm)AlertDialog(onDismissRequest={if(!state.busy)confirm=false},title={Text("Xác nhận chốt bill")},
        text={Text("Tổng: ${state.preview?.grandTotal?.let(::passengerMoney).orEmpty()}\nSau khi chốt sẽ không ghi thêm hoặc điều chỉnh khoản chi trên bill này.")},
        confirmButton={TextButton(onClick={confirm=false;model.confirm()},enabled=!state.busy){Text("Xác nhận chốt")}},
        dismissButton={TextButton(onClick={confirm=false},enabled=!state.busy){Text("Kiểm tra lại")}})
}
