package com.project.tour.model.enums.convenience;

public enum ServiceTourStatus {

    WAITING_CONFIG, // Operation đã phân công, Convenience chưa cấu hình

    NOT_STARTED, // Đã cấu hình, chờ tour chạy

    IN_PROGRESS, // Tour đang hoạt động

    COMPLETED // Tour kết thúc
}