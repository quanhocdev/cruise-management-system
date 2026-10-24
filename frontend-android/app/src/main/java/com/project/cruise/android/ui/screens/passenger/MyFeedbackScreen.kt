package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.data.repository.feedbackStatusLabel
import com.project.cruise.android.data.repository.feedbackTargetLabel
import com.project.cruise.android.data.repository.feedbackAvailability
import com.project.cruise.android.data.dto.feedback.FeedbackDraft
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

@Composable
fun MyFeedbackScreen(bookingId: Long, model: FeedbackViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()
    var selected by rememberSaveable(bookingId){mutableStateOf<Long?>(null)}
    var rating by rememberSaveable(bookingId){mutableStateOf(5)}
    var content by rememberSaveable(bookingId){mutableStateOf("")}
    var confirm by rememberSaveable(bookingId){mutableStateOf(false)}
    LaunchedEffect(bookingId) { model.load(bookingId) }

    PassengerPage("Đánh giá của tôi", onBack = onBack) {
        item {
            OceanBanner(
                "TRẢI NGHIỆM CHUYẾN ĐI",
                "Những điều bạn chia sẻ",
                "Xem lại nhận xét và trạng thái các đánh giá của bạn trong chuyến này.",
            )
        }
        state.message?.let{item{Text(it)}}
        state.optionsError?.let{item{PassengerError(it){model.load(bookingId)}}}
        state.options?.let{options->item{PassengerCard{
            Text("Đánh giá chuyến đi",style=MaterialTheme.typography.titleLarge)
            Text(feedbackAvailability(options.state))
            if(options.state=="READY" && state.pending==null){
                Text("Chọn hành khách đã hoàn tất chuyến")
                options.passengers.filter{it.participated}.forEach{guest->
                    FilterChip(selected=selected==guest.bookingPassengerId,onClick={selected=guest.bookingPassengerId},
                        enabled=!state.sending,label={Text(guest.name)})
                }
                Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){
                    (1..5).forEach{value->FilterChip(selected=rating==value,onClick={rating=value},enabled=!state.sending,label={Text("$value ★")})}
                }
                OutlinedTextField(content,{if(it.length<=5000)content=it},label={Text("Chia sẻ trải nghiệm của bạn")},
                    supportingText={Text("${content.length}/5000")},minLines=3,enabled=!state.sending,modifier=Modifier.fillMaxWidth())
                Button(onClick={confirm=true},enabled=!state.sending && selected!=null && content.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Gửi đánh giá")}
            }
            TextButton(onClick={model.load(bookingId)},enabled=!state.sending){Text("Cập nhật điều kiện")}
        }}}
        state.pending?.let{draft->item{PassengerCard{
            Text("Đánh giá đang chờ kết quả",style=MaterialTheme.typography.titleMedium)
            Text("${draft.rating}/5 · ${draft.content}")
            Button(onClick={model.submit(draft)},enabled=!state.sending){Text(if(state.sending)"Đang gửi…" else "Gửi lại đánh giá")}
        }}}
        when {
            state.loading -> item { PassengerLoading() }
            state.error != null -> item { PassengerError(state.error!!) { model.load(bookingId) } }
            state.items.isEmpty() -> item {
                PassengerEmpty("Chưa có đánh giá", "Bạn chưa có đánh giá nào cho chuyến này.")
            }
            else -> {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${state.items.size} đánh giá", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { model.load(bookingId) }) { Text("Làm mới") }
                    }
                }
                items(state.items, key = { it.id }) { review ->
                    PassengerCard {
                        Text(feedbackTargetLabel(review.targetType), style = MaterialTheme.typography.titleMedium)
                        Text("${"★".repeat(review.rating.coerceIn(0, 5))}  ${review.rating}/5",
                            color = MaterialTheme.colorScheme.primary)
                        Text(review.content, style = MaterialTheme.typography.bodyLarge)
                        Text("${feedbackStatusLabel(review.status)} · ${passengerDate(review.createdAt)}",
                            style = MaterialTheme.typography.bodySmall)
                        review.moderationReason?.takeIf { it.isNotBlank() }?.let {
                            Text("Lý do: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        if (!review.imageUrls.isNullOrEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(review.imageUrls.take(5)) { url ->
                                    AsyncImage(model = url, contentDescription = "Ảnh đính kèm đánh giá",
                                        modifier = Modifier.size(96.dp), contentScale = ContentScale.Crop)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(confirm)AlertDialog(onDismissRequest={confirm=false},title={Text("Gửi đánh giá chuyến đi?")},
        text={Text("$rating/5\n${content.trim()}\nĐánh giá sẽ được hiển thị công khai.")},
        confirmButton={TextButton(onClick={selected?.let{model.submit(FeedbackDraft(bookingId,it,rating,content.trim()))};confirm=false}){Text("Xác nhận gửi")}},
        dismissButton={TextButton(onClick={confirm=false}){Text("Chỉnh lại")}})
}
