
package com.project.cruise.android.viewmodel.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffResponseRequest
import com.project.cruise.android.data.repository.StaffAssignmentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

class StaffAssignmentsViewModel(
    private val repository: StaffAssignmentRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(
        StaffAssignmentsState()
    )

    val state = mutableState.asStateFlow()

    private val requestedTours = mutableSetOf<String>()
    private val requestedDetails = mutableSetOf<String>()

    init {
        refresh()
    }

    fun select(id: String?) {
        if (mutableState.value.responding) return

        mutableState.value = mutableState.value.copy(
            selectedId = id,
            message = null,
            error = null
        )

        mutableState.value.selected?.let { item ->
            item.tourId?.let(::loadTour)
            loadAssignmentDetail(item)
        }
    }

    fun refresh() {
        if (mutableState.value.busy) return

        mutableState.value = mutableState.value.copy(
            loading = true,
            error = null,
            message = null,
            requiresRefresh = false
        )

        viewModelScope.launch {
            try {
                val rows = repository.assignments()

                mutableState.value = mutableState.value.copy(
                    assignments = rows,
                    loading = false,
                    refreshed = true,
                    requiresRefresh = false
                )

                rows.mapNotNull { it.tourId }
                    .distinct()
                    .filter(::isValidUuid)
                    .forEach(::loadTour)

                rows.forEach(::loadAssignmentDetail)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(
                    loading = false,
                    requiresRefresh = true,
                    error = errorMessage(e)
                )
            }
        }
    }

    fun respond(
        accept: Boolean,
        reason: String = ""
    ) {
        val before = mutableState.value
        val item = before.selected ?: return

        if (!before.canRespond(item)) return

        if (!accept && reason.isBlank()) {
            mutableState.value = before.copy(
                error = "Vui lòng nhập lý do từ chối"
            )
            return
        }

        val request = StaffResponseRequest.create(
            accept = accept,
            reason = reason
        )

        mutableState.value = before.copy(
            responding = true,
            error = null,
            message = null
        )

        viewModelScope.launch {
            try {
                val updated = repository.respond(
                    id = requireNotNull(item.id),
                    request = request
                )

                check(
                    updated.id == item.id &&
                            updated.status == request.status
                ) {
                    "Phản hồi không khớp với dữ liệu máy chủ"
                }

                mutableState.value = mutableState.value.copy(
                    assignments = mutableState.value.assignments.map {
                        if (it.id == item.id) updated else it
                    },
                    responding = false,
                    message = if (accept) {
                        "Đã chấp nhận phân công"
                    } else {
                        "Đã từ chối phân công"
                    }
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                // PATCH có thể đã thành công ở server nhưng response bị mất.
                // Đọc lại danh sách để xác minh, không gửi PATCH lần thứ hai.
                try {
                    val rows = repository.assignments()

                    val current = rows.firstOrNull {
                        it.id == item.id
                    }

                    val confirmed = current?.status == request.status

                    mutableState.value = mutableState.value.copy(
                        assignments = rows,
                        responding = false,
                        requiresRefresh = false,
                        refreshed = true,
                        error = if (confirmed) {
                            null
                        } else {
                            errorMessage(e)
                        },
                        message = if (confirmed) {
                            "Đã cập nhật trạng thái từ máy chủ"
                        } else {
                            null
                        }
                    )

                    rows.forEach(::loadAssignmentDetail)

                } catch (cancelled: CancellationException) {
                    throw cancelled

                } catch (_: Exception) {
                    mutableState.value = mutableState.value.copy(
                        responding = false,
                        requiresRefresh = true,
                        error = "Chưa xác định được kết quả. Hãy tải lại để kiểm tra trước khi phản hồi tiếp."
                    )
                }
            }
        }
    }

    private fun loadTour(id: String) {
        if (!isValidUuid(id)) return
        if (!requestedTours.add(id)) return

        viewModelScope.launch {
            try {
                val tour = repository.tour(id)

                if (tour.id == id) {
                    mutableState.value = mutableState.value.copy(
                        tours = mutableState.value.tours + (id to tour)
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                requestedTours.remove(id)
            }
        }
    }

    private fun loadAssignmentDetail(item: StaffAssignment) {
        val assignmentId = item.id ?: return
        val tourId = item.tourId ?: return

        if (!isValidUuid(assignmentId) || !isValidUuid(tourId)) {
            return
        }

        val requestKey = "$assignmentId:${item.assignmentType}"

        if (!requestedDetails.add(requestKey)) return

        viewModelScope.launch {
            try {
                when (item.assignmentType) {
                    "PRODUCT" -> {
                        val rows = repository.products(tourId)

                        val target = rows.firstOrNull {
                            it.id == item.targetId
                        }

                        if (target != null) {
                            mutableState.value = mutableState.value.copy(
                                products = mutableState.value.products +
                                        (target.id to target)
                            )
                        }
                    }

                    "SERVICE" -> {
                        val rows = repository.services(tourId)

                        val target = rows.firstOrNull {
                            it.id == item.targetId
                        }

                        if (target != null) {
                            mutableState.value = mutableState.value.copy(
                                services = mutableState.value.services +
                                        (target.id to target)
                            )
                        }
                    }

                    "ACTIVITY_CRUISE" -> {
                        val rows = repository.onboardActivities(tourId)

                        val target = rows.firstOrNull {
                            it.id == item.activityCruiseTourId
                        }

                        if (target != null) {
                            mutableState.value = mutableState.value.copy(
                                onboardActivities =
                                    mutableState.value.onboardActivities +
                                            (target.id to target)
                            )
                        }
                    }

                    "PATROL" -> {
                        // Không cần gọi API chi tiết tầng tàu ở đây.
                        // Screen hiển thị cruiseDeckId làm mã tham chiếu.
                    }

                    else -> {
                        if (item.visitTourId != null) {
                            val rows = repository.shoreActivities(tourId)

                            val target = rows.firstOrNull {
                                it.id == item.visitTourId
                            }

                            if (target != null) {
                                mutableState.value = mutableState.value.copy(
                                    shoreActivities =
                                        mutableState.value.shoreActivities +
                                                (target.id to target)
                                )
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                requestedDetails.remove(requestKey)
            }
        }
    }
}

private fun isValidUuid(value: String?): Boolean {
    if (value.isNullOrBlank()) return false

    return runCatching {
        UUID.fromString(value)
    }.isSuccess
}

private fun errorMessage(e: Exception): String {
    return when ((e as? HttpException)?.code()) {
        401 ->
            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."

        403 ->
            "Tài khoản chưa có quyền thực hiện thao tác này."

        404, 409 ->
            "Phân công đã thay đổi hoặc không còn chờ phản hồi. Danh sách đã được cập nhật."

        400 ->
            "Máy chủ chưa chấp nhận phản hồi. Kiểm tra lý do và tải lại phân công."

        else ->
            "Không thể kết nối hoặc tải dữ liệu. Vui lòng thử tải lại."
    }
}
