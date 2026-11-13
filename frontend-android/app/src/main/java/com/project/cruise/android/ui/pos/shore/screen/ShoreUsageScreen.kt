package com.project.cruise.android.ui.pos.shore.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.ui.components.SuccessSoundEffect
import com.project.cruise.android.ui.components.usage.SectionTitle
import com.project.cruise.android.ui.components.usage.SelectableList
import com.project.cruise.android.ui.components.usage.UsageScreenScaffold
import com.project.cruise.android.ui.pos.shore.components.ShoreActivityVisitCard
import com.project.cruise.android.viewmodel.shore.ShoreUsageViewModel

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

    var selectedActivity by remember {
        mutableStateOf<ActivityVisitTourResponse?>(null)
    }

    SuccessSoundEffect(uiState.successMessage) { viewModel.clearSuccess() }

    LaunchedEffect(tourId) {
        selectedActivity = null
        viewModel.clearError()
        viewModel.loadActivities(tourId)
    }

    UsageScreenScaffold(
        title = "Hoạt động tham quan",
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
                    visitTourId = activity.id
                )
            }
        },
        onBackClick = onBackClick
    ) {
        SectionTitle("Chọn chuyến tham quan")

        SelectableList(
            items = uiState.activities,
            isLoading = uiState.isLoading,
            emptyText = "Tour này không có chuyến tham quan bờ.",
            key = { it.id }
        ) { activity ->
            ShoreActivityVisitCard(
                activity = activity,
                selected = selectedActivity?.id == activity.id,
                onClick = { selectedActivity = activity }
            )
        }
    }
}