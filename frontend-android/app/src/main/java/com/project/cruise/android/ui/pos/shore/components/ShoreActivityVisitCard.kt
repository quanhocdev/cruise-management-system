package com.project.cruise.android.ui.pos.shore.components

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.ui.pos.common.components.ActivityInfoCard
import com.project.cruise.android.ui.pos.common.components.toFact

@Composable
fun ShoreActivityVisitCard(
    activity: ActivityVisitTourResponse,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ActivityInfoCard(
        title = activity.name ?: "Không có tên chuyến tham quan",
        description = activity.description,
        facts =
            listOfNotNull(
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