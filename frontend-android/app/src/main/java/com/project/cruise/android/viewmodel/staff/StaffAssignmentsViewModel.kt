package com.project.cruise.android.viewmodel.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.staff.*
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.repository.StaffAssignmentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class StaffAssignmentsState(
    val assignments: List<StaffAssignment> = emptyList(),
    val loading: Boolean = false,
    val responding: Boolean = false,
    val refreshed: Boolean = false,
    val requiresRefresh: Boolean = false,
    val selectedId: String? = null,
    val message: String? = null,
    val error: String? = null,
    val tours: Map<String, PublicTourDetailResponse> = emptyMap()
) {
    val selected: StaffAssignment? get() = assignments.firstOrNull { it.id == selectedId }
    val busy: Boolean get() = loading || responding
    fun canRespond(item: StaffAssignment): Boolean = refreshed && !busy && !requiresRefresh && item.canRespond
}

class StaffAssignmentsViewModel(private val repository: StaffAssignmentRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(StaffAssignmentsState())
    val state = mutableState.asStateFlow()
    private val requestedTours = mutableSetOf<String>()

    init { refresh() }

    fun select(id: String?) {
        if (mutableState.value.responding) return
        mutableState.value = mutableState.value.copy(selectedId = id, message = null)
        mutableState.value.selected?.tourId?.let { loadTour(it) }
    }

    fun refresh() {
        if (mutableState.value.busy) return
        mutableState.value = mutableState.value.copy(loading = true, error = null, message = null)
        viewModelScope.launch {
            try {
                val rows = repository.assignments()
                mutableState.value = mutableState.value.copy(assignments = rows, loading = false,
                    refreshed = true, requiresRefresh = false)
                rows.mapNotNull { it.tourId }.distinct().take(20).forEach { loadTour(it) }
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(loading = false, requiresRefresh = true, error = errorMessage(e))
            }
        }
    }

    fun respond(accept: Boolean, reason: String = "") {
        val before = mutableState.value
        val item = before.selected ?: return
        if (!before.canRespond(item)) return
        if (!accept && reason.isBlank()) {
            mutableState.value = before.copy(error = "Vui lòng nhập lý do từ chối")
            return
        }
        val request = StaffResponseRequest.create(accept, reason)
        mutableState.value = before.copy(responding = true, error = null, message = null)
        viewModelScope.launch {
            try {
                val updated = repository.respond(requireNotNull(item.id), request)
                check(updated.id == item.id && updated.status == request.status)
                mutableState.value = mutableState.value.copy(
                    assignments = mutableState.value.assignments.map { if (it.id == item.id) updated else it },
                    responding = false, message = if (accept) "Đã chấp nhận phân công" else "Đã từ chối phân công")
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                // A lost response does not mean the server rejected the write. Read back; never resend automatically.
                try {
                    val rows = repository.assignments()
                    val current = rows.firstOrNull { it.id == item.id }
                    val confirmed = current?.status == request.status
                    mutableState.value = mutableState.value.copy(assignments = rows, responding = false,
                        requiresRefresh = false, error = if (confirmed) null else errorMessage(e),
                        message = if (confirmed) "Đã cập nhật trạng thái từ máy chủ" else null)
                } catch (cancelled: CancellationException) { throw cancelled
                } catch (_: Exception) {
                    mutableState.value = mutableState.value.copy(responding = false, requiresRefresh = true,
                        error = "Chưa xác định được kết quả. Hãy tải lại để kiểm tra trước khi phản hồi tiếp.")
                }
            }
        }
    }

    private fun loadTour(id: String) {
        if (!isUuid(id) || !requestedTours.add(id)) return
        viewModelScope.launch {
            try {
                val tour = repository.tour(id)
                if (tour.id == id) mutableState.value = mutableState.value.copy(tours = mutableState.value.tours + (id to tour))
            } catch (e: CancellationException) { throw e
            } catch (_: Exception) { requestedTours.remove(id) }
        }
    }

    private fun errorMessage(e: Exception): String = when ((e as? HttpException)?.code()) {
        401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng xuất và đăng nhập lại."
        403 -> "Tài khoản chưa có quyền thực hiện thao tác này."
        404, 409 -> "Phân công đã thay đổi hoặc không còn chờ phản hồi. Danh sách đã được cập nhật."
        400 -> "Máy chủ chưa chấp nhận phản hồi. Kiểm tra lý do và tải lại phân công."
        else -> "Không thể kết nối hoặc tải dữ liệu. Vui lòng thử tải lại."
    }
}
