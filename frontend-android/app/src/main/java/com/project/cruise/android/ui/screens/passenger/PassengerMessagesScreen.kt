
package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.chat.ConfirmedBookingResponse
import com.project.cruise.android.data.dto.chat.ConversationResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.chat.MessagingViewModel

private val departments = listOf(
    "ONBOARD" to "Nhân viên trên tàu",
    "CONVENIENCE" to "Dịch vụ tiện ích",
    "SHORE" to "Hoạt động trên bờ"
)

@Composable
fun PassengerMessagesScreen(
    viewModel: MessagingViewModel,
    onConversationClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        viewModel.loadBookings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("Quay lại")
            }

            Text(
                text = "Tin nhắn",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OceanNavy
            )

            Spacer(Modifier.weight(1f))

            Button(onClick = { showCreateDialog = true }) {
                Text("Tạo mới")
            }
        }

        Spacer(Modifier.height(16.dp))

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        if (state.conversations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Bạn chưa có cuộc trò chuyện nào.",
                        color = OceanSlate
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { showCreateDialog = true }) {
                        Text("Bắt đầu trò chuyện")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.conversations,
                    key = { it.id }
                ) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            onConversationClick(conversation.id)
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateConversationDialog(
            bookings = state.bookings,
            onDismiss = { showCreateDialog = false },
            onCreate = { bookingId, department ->
                viewModel.createOrGetConversation(
                    bookingId = bookingId,
                    department = department
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun ConversationItem(
    conversation: ConversationResponse,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = departments.firstOrNull {
                    it.first == conversation.department
                }?.second ?: conversation.department,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Booking: ${conversation.bookingCode ?: conversation.bookingId}",
                color = OceanSlate
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Trạng thái: ${conversation.status}",
                color = OceanSlate
            )

            conversation.updatedAt?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Cập nhật: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate
                )
            }
        }
    }
}

@Composable
private fun CreateConversationDialog(
    bookings: List<ConfirmedBookingResponse>,
    onDismiss: () -> Unit,
    onCreate: (Long, String) -> Unit
) {
    var selectedBooking by remember { mutableStateOf<ConfirmedBookingResponse?>(null) }
    var selectedDepartment by remember { mutableStateOf("ONBOARD") }
    var bookingExpanded by remember { mutableStateOf(false) }
    var departmentExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo cuộc trò chuyện") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Chọn booking đã xác nhận")

                Box {
                    OutlinedButton(
                        onClick = { bookingExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            selectedBooking?.let {
                                it.bookingCode ?: "Booking #${it.bookingId}"
                            } ?: "Chọn booking"
                        )
                    }

                    DropdownMenu(
                        expanded = bookingExpanded,
                        onDismissRequest = { bookingExpanded = false }
                    ) {
                        bookings.forEach { booking ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        booking.bookingCode
                                            ?: "Booking #${booking.bookingId}"
                                    )
                                },
                                onClick = {
                                    selectedBooking = booking
                                    bookingExpanded = false
                                }
                            )
                        }
                    }
                }

                Text("Chọn phòng ban")

                Box {
                    OutlinedButton(
                        onClick = { departmentExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            departments.first {
                                it.first == selectedDepartment
                            }.second
                        )
                    }

                    DropdownMenu(
                        expanded = departmentExpanded,
                        onDismissRequest = { departmentExpanded = false }
                    ) {
                        departments.forEach { department ->
                            DropdownMenuItem(
                                text = { Text(department.second) },
                                onClick = {
                                    selectedDepartment = department.first
                                    departmentExpanded = false
                                }
                            )
                        }
                    }
                }

                if (bookings.isEmpty()) {
                    Text(
                        "Bạn chưa có booking đã xác nhận.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedBooking != null,
                onClick = {
                    selectedBooking?.let {
                        onCreate(it.bookingId, selectedDepartment)
                    }
                }
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
