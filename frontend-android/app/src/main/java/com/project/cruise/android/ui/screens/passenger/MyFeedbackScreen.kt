package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.data.repository.feedbackStatusLabel
import com.project.cruise.android.data.repository.feedbackTargetLabel
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

@Composable
fun MyFeedbackScreen(bookingId: Long, model: FeedbackViewModel, onBack: () -> Unit) {
    val state by model.state.collectAsState()
    LaunchedEffect(bookingId) { model.load(bookingId) }

    PassengerPage("Đánh giá của tôi", onBack = onBack) {
        item {
            OceanBanner(
                "TRẢI NGHIỆM CHUYẾN ĐI",
                "Những điều bạn chia sẻ",
                "Xem lại nhận xét và trạng thái các đánh giá của bạn trong chuyến này.",
            )
        }
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
}
