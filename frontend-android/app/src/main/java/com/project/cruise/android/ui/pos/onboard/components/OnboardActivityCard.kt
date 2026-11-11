package com.project.cruise.android.ui.pos.onboard.components

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.ui.pos.common.components.ActivityInfoCard
import com.project.cruise.android.ui.pos.common.components.toFact

@Composable
fun OnboardActivityCard(
    activity: ActivityCruiseTourResponse,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ActivityInfoCard(
        title = activity.activityCruiseName ?: "Không có tên hoạt động",
        description = activity.activityCruiseDescription,
        facts =
            listOfNotNull(
                activity.cruiseAreaName.toFact("Khu vực"),
                activity.startTime.toFact("Bắt đầu"),
                activity.endTime.toFact("Kết thúc"),
                activity.maxPassengers.toFact("Tối đa", suffix = " người"),
            ),
        price = activity.price,
        status = activity.status?.toString(),
        selected = selected,
        onClick = onClick,
    )
}