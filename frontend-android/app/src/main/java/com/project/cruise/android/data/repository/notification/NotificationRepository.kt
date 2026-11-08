package com.project.cruise.android.data.repository.notification

import com.project.cruise.android.data.dto.passenger.PassengerNotification
import com.project.cruise.android.data.network.api.NotificationApiService

interface NotificationSource {
    suspend fun list(): List<PassengerNotification>
    suspend fun read(id: Long): PassengerNotification
    suspend fun readAll()
}

class NotificationRepository(
    private val api: NotificationApiService // Đổi sang NotificationApiService
) : NotificationSource {
    // Lưu ý: Đảm bảo trong NotificationApiService hoặc endpoint tương ứng có các hàm này,
    // nếu các hàm getNotifications() / readNotification() nằm ở service khác, bạn có thể trỏ đúng service đó.
    override suspend fun list(): List<PassengerNotification> {
        // Nếu dùng getActivityNotices() thì điều chỉnh lại theo model DTO thực tế của bạn
        TODO("Mapping lại hàm gọi tương ứng từ NotificationApiService")
    }

    override suspend fun read(id: Long): PassengerNotification {
        TODO("Mapping lại hàm gọi tương ứng từ NotificationApiService")
    }

    override suspend fun readAll() {
        TODO("Mapping lại hàm gọi tương ứng từ NotificationApiService")
    }
}