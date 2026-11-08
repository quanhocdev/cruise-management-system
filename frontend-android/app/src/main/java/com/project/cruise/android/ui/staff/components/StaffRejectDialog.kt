
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StaffRejectDialog(
    confirmation: String,
    reason: String,
    canRespond: Boolean,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    val accepting = confirmation == "ACCEPTED"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (accepting) {
                    "Nhận công việc này?"
                } else {
                    "Từ chối phân công?"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (accepting) {
                        "Xác nhận bạn nhận phân công này. Sau khi gửi, bạn không thể tự đổi phản hồi."
                    } else {
                        "Cho người phụ trách biết lý do bạn không thể nhận công việc."
                    }
                )

                if (!accepting) {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = onReasonChange,
                        label = {
                            Text("Lý do từ chối (bắt buộc)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canRespond &&
                        (accepting || reason.isNotBlank()),
                onClick = {
                    onConfirm(accepting)
                }
            ) {
                Text(
                    if (accepting) {
                        "Xác nhận nhận việc"
                    } else {
                        "Gửi lý do từ chối"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Quay lại")
            }
        }
    )
}
