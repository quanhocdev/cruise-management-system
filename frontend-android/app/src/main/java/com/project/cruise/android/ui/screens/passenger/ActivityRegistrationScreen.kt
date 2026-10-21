package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.*


@Composable
fun ActivityRegistrationScreen(bookingId: Long,model: ActivityRegistrationViewModel,onBack: ()->Unit,notificationButton: @Composable ()->Unit = {}){
    val state by model.state.collectAsState()
    LaunchedEffect(bookingId){model.load(bookingId)}
    var guestId by rememberSaveable{mutableStateOf<Long?>(null)}
    var selected by remember{mutableStateOf<RegistrationChoice?>(null)}
    var cancelId by remember{mutableStateOf<Long?>(null)}
    val guest=state.catalog?.passengers?.find{it.id==guestId}?:state.catalog?.passengers?.firstOrNull()
    LaunchedEffect(state.message){if(state.message!=null){selected=null;cancelId=null}}
    PassengerPage("Hoạt động trong chuyến",onBack=onBack){
        item{OceanBanner("TRẢI NGHIỆM",state.catalog?.bookingCode?:"Hoạt động & tham quan bờ","Đăng ký để giữ chỗ. Chi phí và quyền lợi được tính khi nhân viên xác nhận tham gia.")}
        item{OutlinedButton(onClick={model.load(bookingId,state.page)},enabled=!state.busy){Text("Cập nhật")}}
        if(state.busy)item{PassengerLoading()}
        state.error?.let{item{PassengerError(it){model.load(bookingId,state.page)}}}
        state.message?.let{item{Text(it)}}
        state.catalog?.let{catalog->
            item{PassengerSection("Chọn hành khách")}
            catalog.passengers.forEach{p->item{FilterChip(selected=p.id==guest?.id,onClick={guestId=p.id},enabled=!state.busy,label={Text(p.name)})}}
            item{PassengerSection("Lịch hoạt động","Giá hiển thị là giá niêm yết. Quyền lợi trong gói được kiểm tra khi tham gia.")}
            if(catalog.activities.isEmpty())item{Text("Chuyến này chưa có hoạt động mở đăng ký.")}
            catalog.activities.forEach{choice->item{PassengerCard{
                Text(choice.target.name,style=MaterialTheme.typography.titleMedium)
                Text(if(choice.target.type=="ACTIVITY")"Trên tàu" else "Tham quan bờ")
                Text("${choice.target.startTime.orEmpty().replace('T',' ')} → ${choice.target.endTime.orEmpty().replace('T',' ')}")
                Text("${passengerMoney(choice.target.price)} · Còn ${choice.remainingSeats} chỗ")
                val already=state.items.any{it.bookingPassengerId==guest?.id && it.targetId==choice.target.id && it.targetType==choice.target.type && it.status!="CANCELLED"}
                Button(onClick={selected=choice},enabled=!state.busy && !already && RegistrationDraft.canRegister(choice,guest)){Text(if(already)"Đã đăng ký" else "Đăng ký giữ chỗ")}
            }}}
        }
        item{PassengerSection("Đăng ký của booking","Nút hủy xuất hiện khi còn trong hạn hủy tự động. Sau thời hạn này, vui lòng liên hệ nhân viên.")}
        state.items.forEach{r->item{PassengerCard{
            Text(r.name,style=MaterialTheme.typography.titleMedium);Text(r.passengerName)
            Text(RegistrationDraft.status(r.status));Text(r.startTime.replace('T',' '))
            if(r.canCancel)OutlinedButton(onClick={cancelId=r.id},enabled=!state.busy){Text("Hủy đăng ký")}
        }}}
        item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            TextButton(onClick={model.load(bookingId,state.page-1)},enabled=!state.busy && state.page>0){Text("Trước")}
            TextButton(onClick={model.load(bookingId,state.page+1)},enabled=!state.busy && state.items.size==20){Text("Tiếp")}
        }}
        item{notificationButton()}
    }
    if(selected!=null && guest!=null)AlertDialog(onDismissRequest={if(!state.busy)selected=null},title={Text("Xác nhận giữ chỗ")},
        text={Text("${guest.name}\n${selected!!.target.name}\nChưa thu phí ở bước đăng ký.\n${state.error.orEmpty()}")},
        confirmButton={TextButton(onClick={model.register(bookingId,guest,selected!!)},enabled=!state.busy){Text("Xác nhận")}},
        dismissButton={TextButton(onClick={selected=null},enabled=!state.busy){Text("Quay lại")}})
    if(cancelId!=null)AlertDialog(onDismissRequest={if(!state.busy)cancelId=null},title={Text("Hủy chỗ đã đăng ký?")},text={Text("Chỗ sẽ được trả lại cho người khác. ${state.error.orEmpty()}")},
        confirmButton={TextButton(onClick={model.cancel(bookingId,cancelId!!)},enabled=!state.busy){Text("Xác nhận hủy")}},
        dismissButton={TextButton(onClick={cancelId=null},enabled=!state.busy){Text("Giữ đăng ký")}})
}
