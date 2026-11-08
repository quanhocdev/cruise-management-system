
package com.project.cruise.android.viewmodel.staff

import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse

data class StaffAssignmentsState(
    val assignments: List<StaffAssignment> = emptyList(),
    val selectedId: String? = null,

    val tours: Map<String, PublicTourDetailResponse> = emptyMap(),
    val products: Map<String, ProductTourResponse> = emptyMap(),
    val services: Map<String, ServiceTourResponse> = emptyMap(),
    val onboardActivities: Map<String, ActivityCruiseTourResponse> = emptyMap(),
    val shoreActivities: Map<String, ActivityVisitTourResponse> = emptyMap(),

    val loading: Boolean = false,
    val responding: Boolean = false,
    val refreshed: Boolean = false,
    val requiresRefresh: Boolean = false,

    val error: String? = null,
    val message: String? = null
) {
    val selected: StaffAssignment?
        get() = assignments.firstOrNull {
            it.id == selectedId
        }

    val busy: Boolean
        get() = loading || responding

    fun canRespond(item: StaffAssignment): Boolean {
        return refreshed &&
                !busy &&
                !requiresRefresh &&
                item.canRespond &&
                assignments.any {
                    it.id == item.id
                }
    }

    fun productOf(item: StaffAssignment): ProductTourResponse? {
        val id = item.targetId ?: return null
        return products[id]
    }

    fun serviceOf(item: StaffAssignment): ServiceTourResponse? {
        val id = item.targetId ?: return null
        return services[id]
    }

    fun onboardActivityOf(
        item: StaffAssignment
    ): ActivityCruiseTourResponse? {
        val id = item.activityCruiseTourId
            ?: item.targetId
            ?: return null

        return onboardActivities[id]
    }

    fun shoreActivityOf(
        item: StaffAssignment
    ): ActivityVisitTourResponse? {
        val id = item.visitTourId
            ?: item.targetId
            ?: return null

        return shoreActivities[id]
    }

    fun targetName(item: StaffAssignment): String? {
        return when (item.assignmentType) {
            "PRODUCT" -> productOf(item)?.productName
            "SERVICE" -> serviceOf(item)?.serviceName
            "ACTIVITY_CRUISE" ->
                onboardActivityOf(item)?.activityCruiseName
            "PATROL" -> "Tuần tra tầng tàu"
            else -> shoreActivityOf(item)?.name
        }
    }
}
