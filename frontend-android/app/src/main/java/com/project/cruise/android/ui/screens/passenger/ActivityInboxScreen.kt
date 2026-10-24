package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.project.cruise.android.data.repository.ActivityInboxSource
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.ActivityInboxViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Composable
fun ActivityInboxButton(source: ActivityInboxSource,onClick: ()->Unit) {
    var count by remember(source) { mutableStateOf<Long?>(null) }
    val owner=LocalLifecycleOwner.current
    LaunchedEffect(source,owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while(true) {
                try { count=source.unreadCount() }
                catch(e:CancellationException) { throw e }
                catch(e:Exception) { count=null }
                delay(45000)
            }
        }
    }
    OutlinedButton(onClick=onClick,modifier=Modifier.fillMaxWidth()) {
        Text("Thông báo hoạt động"+(count?.let { " · $it chưa đọc" }?:""))
    }
}

@Composable
fun ActivityInboxScreen(model: ActivityInboxViewModel,onBooking: (Long)->Unit,onBack: ()->Unit) {
    val state by model.state.collectAsState()
    val owner=LocalLifecycleOwner.current
    LaunchedEffect(model,owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while(true) { model.refresh();delay(45000) }
        }
    }
    PassengerPage("Thông báo hoạt động",onBack=onBack) {
        item { OceanBanner("HỘP THƯ CHUYẾN ĐI","Lịch trình luôn trong tầm tay",
            "Xác nhận giữ chỗ, thay đổi lịch và các cập nhật dành cho booking của bạn.") }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text(state.unreadCount?.let { "$it thông báo chưa đọc" }?:"Chưa cập nhật số chưa đọc")
                TextButton(onClick={model.refresh()},enabled=!state.loading && state.reading==null) { Text("Làm mới") }
            }
        }
        item {
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                FilterChip(selected=!state.unreadOnly,onClick={model.refresh(0,false)},enabled=state.reading==null,label={Text("Tất cả")})
                FilterChip(selected=state.unreadOnly,onClick={model.refresh(0,true)},enabled=state.reading==null,label={Text("Chưa đọc")})
            }
        }
        if(state.loading)item { PassengerLoading() }
        state.error?.let { item { PassengerError(it) { model.refresh() } } }
        if(!state.loading && state.error==null && state.items.isEmpty())item {
            PassengerEmpty(if(state.unreadOnly)"Bạn đã đọc hết thông báo trên trang này" else "Chưa có thông báo trên trang này",
                "Thông báo mới sẽ được cập nhật khi bạn mở hộp thư.")
        }
        items(state.items,key={it.id}) { notice ->
            PassengerCard {
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Text(if(notice.priority=="URGENT")"KHẨN" else "THÔNG TIN",
                        color=if(notice.priority=="URGENT")MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    Text(if(notice.readAt==null)"Chưa đọc" else "Đã đọc",style=MaterialTheme.typography.labelMedium)
                }
                Text(notice.title,style=MaterialTheme.typography.titleMedium)
                Text(notice.message,style=MaterialTheme.typography.bodyMedium,maxLines=3)
                Text(passengerDate(notice.createdAt),style=MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick={model.open(notice)},enabled=state.reading==null,modifier=Modifier.fillMaxWidth()) {
                    Text(if(state.reading==notice.id)"Đang xác nhận…" else "Đọc thông báo")
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                TextButton(onClick={model.refresh(state.page-1)},enabled=state.page>0 && !state.loading && state.reading==null) { Text("Trang trước") }
                Text("Trang ${state.page+1}")
                TextButton(onClick={model.refresh(state.page+1)},enabled=state.items.size==20 && !state.loading && state.reading==null) { Text("Trang sau") }
            }
        }
    }
    state.selected?.let { notice ->
        AlertDialog(onDismissRequest=model::dismiss,title={Text(notice.title)},text={Text(notice.message)},
            confirmButton={
                if(notice.bookingId!=null && notice.bookingId>0)TextButton(onClick={model.dismiss();onBooking(notice.bookingId)}) {
                    Text("Mở booking liên quan")
                } else TextButton(onClick=model::dismiss) { Text("Đã hiểu") }
            },dismissButton={TextButton(onClick=model::dismiss) { Text("Đóng") }})
    }
}
