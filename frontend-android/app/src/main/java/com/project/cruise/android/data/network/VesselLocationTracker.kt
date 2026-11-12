package com.project.cruise.android.data.network
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class VesselLocationTracker(
    private val context: Context,
    private val vesselTrackingApi: VesselTrackingApi
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var trackingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Bắt đầu tracking định kỳ 10 giây/lần
    fun startTracking(tourId: Long, token: String) {
        if (trackingJob?.isActive == true) return // Đang chạy rồi thì không khởi động lại chồng chéo

        trackingJob = scope.launch {
            while (isActive) {
                try {
                    // 1. Lấy vị trí GPS hiện tại của điện thoại
                    val location = getCurrentLocation()
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude

                        // 2. Đóng gói request
                        val request = VesselLocationRequest(latitude = lat, longitude = lng)

                        // 3. Gửi POST request lên Backend
                        val response = vesselTrackingApi.updateVesselLocation(
                            token = "Bearer $token",
                            tourId = tourId,
                            location = request
                        )

                        if (response.isSuccessful) {
                            println("GPS Tracker: Đã gửi vị trí thành công ($lat, $lng)")
                        } else {
                            println("GPS Tracker: Lỗi từ server - Code ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    println("GPS Tracker Exception: ${e.message}")
                }

                // 4. Chờ đúng 10 giây rồi lặp lại vòng lặp tiếp theo
                delay(10_000L)
            }
        }
    }

    // Dừng tracking (ví dụ khi kết thúc tour hoặc thoát màn hình)
    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { _ ->
                continuation.resume(null)
            }
        }
    }
}