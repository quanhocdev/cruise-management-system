package com.project.cruise.android.ui.components.usage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Khung chung của các màn hình "usage" (convenience / onboard / shore):
 * nút quay lại, tiêu đề, thông tin hành khách, [content], thông báo lỗi/thành công, nút xác nhận.
 *
 * [content] nằm trong ColumnScope nên có thể dùng Modifier.weight(1f).
 */
@Composable
fun UsageScreenScaffold(
    title: String,
    passengerName: String,
    bookingId: Long,
    bookingPassengerId: Long,
    isSubmitting: Boolean,
    errorMessage: String?,
    successMessage: String?,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onBackClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick,
            enabled = !isSubmitting
        ) {
            Text("← Quay lại")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        PassengerInfoCard(
            passengerName = passengerName,
            bookingId = bookingId,
            bookingPassengerId = bookingPassengerId
        )

        Spacer(modifier = Modifier.height(20.dp))

        content()

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        successMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onConfirm,
            enabled = confirmEnabled && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Xác nhận")
            }
        }
    }
}

@Composable
fun PassengerInfoCard(
    passengerName: String,
    bookingId: Long,
    bookingPassengerId: Long
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = passengerName.ifBlank { "Không có tên hành khách" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Booking #$bookingId",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Booking Passenger #$bookingPassengerId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Tiêu đề nhỏ của một mục, kèm khoảng cách 8dp phía dưới. */
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
}

/** Danh sách có 3 trạng thái: đang tải / rỗng / có dữ liệu. Chiếm phần không gian còn lại. */
@Composable
fun <T> ColumnScope.SelectableList(
    items: List<T>,
    isLoading: Boolean,
    emptyText: String,
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit
) {
    when {
        isLoading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        items.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = emptyText,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = items, key = key) { item ->
                itemContent(item)
            }
        }
    }
}