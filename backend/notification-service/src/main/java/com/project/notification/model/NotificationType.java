package com.project.notification.model;

public enum NotificationType {
    PENDING_PAYMENT, // Xác nhận đặt tour, chờ thanh toán
    BOOKING_CANCELLED, // Đơn hàng đã bị hủy (đã hủy)
    BOOKING_CONFIRMED, // Đơn hàng đã được xác nhận (đã đặt)
    TOUR_REMINDER // Nhắc nhở lịch trình tour sắp diễn ra
}