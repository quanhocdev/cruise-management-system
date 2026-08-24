package com.project.convenience.model.enums;

public enum ServiceTourStatus {

    WAITING_CONFIG, // Operation đã phân công, chưa cấu hình

    CONFIGURED, // Convenience đã cấu hình xong

    NOT_STARTED, // Tour đã READY, nhưng chưa bắt đầu chạy

    IN_PROGRESS, // Tour đang hoạt động

    COMPLETED // Tour kết thúc
}