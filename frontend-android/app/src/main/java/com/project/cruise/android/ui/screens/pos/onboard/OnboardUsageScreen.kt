package com.project.cruise.android.ui.screens.pos.onboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.ui.components.OptionalInfoText
import com.project.cruise.android.ui.components.SelectableCard
import com.project.cruise.android.ui.components.SuccessSoundEffect
import com.project.cruise.android.ui.components.usage.SectionTitle
import com.project.cruise.android.ui.components.usage.SelectableList
import com.project.cruise.android.ui.components.usage.UsageScreenScaffold
import com.project.cruise.android.viewmodel.onboard.OnboardUsageViewModel

@Composable
fun OnboardUsageScreen(
    bookingPassengerId: Long,
    passengerName: String,
    bookingId: Long,
    tourId: String,
    tourPackageId: String,
    nfcCardUid: String,
    onBackClick: () -> Unit,
    viewModel: OnboardUsageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedActivity by remember {
        mutableStateOf<ActivityCruiseTourResponse?>(null)
    }

    SuccessSoundEffect(uiState.successMessage) { viewModel.clearSuccess() }

    LaunchedEffect(tourId) {
        selectedActivity = null
        viewModel.clearError()
        viewModel.loadActivities(tourId)
    }

    UsageScreenScaffold(
        title = "Hoạt động trên tàu",
        passengerName = passengerName,
        bookingId = bookingId,
        bookingPassengerId = bookingPassengerId,
        isSubmitting = uiState.isSubmitting,
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        confirmEnabled = selectedActivity != null && !uiState.isLoading,
        onConfirm = {
            selectedActivity?.let { activity ->
                viewModel.createUsage(
                    nfcCardUid = nfcCardUid,
                    activityCruiseTourId = activity.id
                )
            }
        },
        onBackClick = onBackClick
    ) {
        SectionTitle("Chọn hoạt động")

        SelectableList(
            items = uiState.activities,
            isLoading = uiState.isLoading,
            emptyText = "Tour này không có hoạt động trên tàu.",
            key = { it.id }
        ) { activity ->
            OnboardActivityCard(
                activity = activity,
                selected = selectedActivity?.id == activity.id,
                onClick = { selectedActivity = activity }
            )
        }
    }
}

@Composable
private fun OnboardActivityCard(
    activity: ActivityCruiseTourResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    SelectableCard(selected = selected, onClick = onClick) {
        Text(
            text = activity.activityCruiseName ?: "Không có tên hoạt động",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OptionalInfoText(
            value = activity.activityCruiseDescription,
            style = MaterialTheme.typography.bodyMedium
        )
        OptionalInfoText(activity.cruiseAreaName, prefix = "Khu vực: ", topSpace = 8.dp)
        OptionalInfoText(activity.startTime, prefix = "Bắt đầu: ")
        OptionalInfoText(activity.endTime, prefix = "Kết thúc: ")
        OptionalInfoText(activity.maxPassengers, prefix = "Số người tối đa: ")
        OptionalInfoText(activity.price, prefix = "Giá: ", topSpace = 8.dp, emphasized = true)
        OptionalInfoText(activity.status, prefix = "Trạng thái: ")
    }
}