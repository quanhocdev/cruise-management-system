package com.project.cruise.android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.project.cruise.android.data.network.VesselTrackingApi
import com.project.cruise.android.data.network.VesselLocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VesselTrackingViewModel(
    private val context: Context,
    private val vesselTrackingApi: VesselTrackingApi
) : ViewModel() {

    private val tracker = VesselLocationTracker(context, vesselTrackingApi)

    // Trạng thái để UI Jetpack Compose biết tàu có đang bật chế độ chia sẻ vị trí hay không
    private val _isTrackingActive = MutableStateFlow(false)
    val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    // Bắt đầu hành trình / bật gửi GPS định kỳ 10 giây/lần
    fun startTourTracking(tourId: Long, token: String) {
        if (_isTrackingActive.value) return

        tracker.startTracking(tourId, token)
        _isTrackingActive.value = true
    }

    // Dừng hành trình / tắt gửi GPS
    fun stopTourTracking() {
        tracker.stopTracking()
        _isTrackingActive.value = false
    }

    override fun onCleared() {
        super.onCleared()
        tracker.stopTracking() // Dọn dẹp job khi ViewModel bị hủy để tránh rò rỉ bộ nhớ
    }
}