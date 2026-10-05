package com.project.cruise.android.ui.screens.pos

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.viewmodel.ShoreUsageViewModel
import kotlinx.coroutines.delay

@Composable
fun ShoreUsageScreen(
    bookingPassengerId: Long,
    passengerName: String,
    bookingId: Long,
    tourId: String,
    tourPackageId: String,
    nfcCardUid: String,
    onBackClick: () -> Unit,
    viewModel: ShoreUsageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage) {

        if (uiState.successMessage != null) {

            val toneGenerator = ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                100
            )

            toneGenerator.startTone(
                ToneGenerator.TONE_PROP_ACK,
                200
            )

            toneGenerator.release()

            delay(2000)

            viewModel.clearSuccess()
        }
    }

    var selectedActivity by remember {
        mutableStateOf<ActivityVisitTourResponse?>(null)
    }

    LaunchedEffect(tourId) {
        viewModel.loadActivities(tourId)
    }

    LaunchedEffect(tourId) {
        selectedActivity = null
        viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBackClick,
            enabled = !uiState.isSubmitting
        ) {
            Text("← Quay lại")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hoạt động tham quan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = passengerName.ifBlank {
                        "Không có tên hành khách"
                    },
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

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chọn chuyến tham quan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            if (uiState.activities.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "Tour này không có chuyến tham quan bờ.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = uiState.activities,
                        key = { it.id }
                    ) { activity ->

                        ShoreActivityVisitCard(
                            activity = activity,
                            selected =
                                selectedActivity?.id == activity.id,
                            onClick = {
                                selectedActivity = activity
                            }
                        )
                    }
                }
            }
        }

        uiState.errorMessage?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        uiState.successMessage?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                val activity = selectedActivity
                    ?: return@Button

                viewModel.createUsage(
                    nfcCardUid = nfcCardUid,
                    visitTourId = activity.id
                )
            },
            enabled =
                selectedActivity != null &&
                        !uiState.isLoading &&
                        !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (uiState.isSubmitting) {

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
private fun ShoreActivityVisitCard(
    activity: ActivityVisitTourResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = activity.name
                    ?: "Không có tên chuyến tham quan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            activity.description
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            activity.startTime
                ?.takeIf { it.isNotBlank() }
                ?.let { startTime ->

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bắt đầu: $startTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            activity.endTime
                ?.takeIf { it.isNotBlank() }
                ?.let { endTime ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kết thúc: $endTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            activity.maxPassengers?.let { maxPassengers ->

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Số người tối đa: $maxPassengers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            activity.price?.let { price ->

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Giá: $price",
                    fontWeight = FontWeight.SemiBold
                )
            }

            activity.status
                ?.takeIf { it.isNotBlank() }
                ?.let { status ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Trạng thái: $status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
        }
    }
}