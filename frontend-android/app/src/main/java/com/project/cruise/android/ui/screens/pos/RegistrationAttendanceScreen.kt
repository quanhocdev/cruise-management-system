package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.project.cruise.android.data.dto.booking.ActivityRegistration
import com.project.cruise.android.ui.components.passengerMoney
import com.project.cruise.android.viewmodel.passenger.*

@Composable
fun RegistrationAttendanceScreen(role: PosRole,model: ActivityRegistrationViewModel,onBack: ()->Unit){
    val state by model.state.collectAsState()
    var code by rememberSaveable{mutableStateOf("")};var search by rememberSaveable{mutableStateOf("")}
    var selected by remember{mutableStateOf<ActivityRegistration?>(null)};var reason by rememberSaveable{mutableStateOf("")}
    LaunchedEffect(state.message){if(state.message!=null){selected=null;reason=""}}
    PosPage{
        PosTopBar("Khách đăng ký hoạt động",role,onBack,!state.busy)
        PosPanel{
            Text(if(role==PosRole.ONBOARD)"Hoạt động trên tàu" else "Chuyến tham quan bờ",style=MaterialTheme.typography.titleLarge)
            OutlinedTextField(code,{code=it},label={Text("Mã booking")},singleLine=true,enabled=!state.busy,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(search,{search=it},label={Text("Tên khách (không bắt buộc)")},enabled=!state.busy,modifier=Modifier.fillMaxWidth())
            PosPrimaryButton("Tra cứu đăng ký",role,{model.staff(role.name,code,search)},!state.busy && code.isNotBlank())
        }
        if(state.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let{PosNotice(it,warning=true)};state.message?.let{PosNotice(it)}
        state.items.forEach{r->PosPanel{
            Text(r.passengerName,style=MaterialTheme.typography.titleLarge);Text("${r.bookingCode} · ${r.name}")
            Text(r.startTime.replace('T',' '));Text(RegistrationDraft.status(r.status))
            Text("Giá niêm yết lúc đăng ký: ${passengerMoney(r.listedPrice)}")
            if(r.status=="CONFIRMED")PosPrimaryButton("Xác nhận tham gia",role,{selected=r;reason=""},!state.busy)
        }}
        Row{
            TextButton(onClick={model.staff(role.name,code,search,state.page-1)},enabled=!state.busy && state.page>0){Text("Trước")}
            TextButton(onClick={model.staff(role.name,code,search,state.page+1)},enabled=!state.busy && state.items.size==20){Text("Tiếp")}
        }
    }
    selected?.let{r->AlertDialog(onDismissRequest={if(!state.busy)selected=null},title={Text("Xác nhận khách tham gia")},
        text={Column{
            Text("${r.passengerName}\n${r.name}\nPhí được ghi theo giá và quyền lợi hiện tại; hệ thống kiểm tra hạn mức trước khi xác nhận.")
            OutlinedTextField(reason,{reason=it},label={Text("Lý do xác nhận thủ công")},enabled=!state.busy)
            state.error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }},confirmButton={TextButton(onClick={model.attend(role.name,r.id,reason)},enabled=!state.busy && RegistrationDraft.validReason(reason)){Text("Xác nhận")}},
        dismissButton={TextButton(onClick={selected=null},enabled=!state.busy){Text("Quay lại")}})}
}
