package com.project.convenience.model.enums;

public enum ProductTourStatus {

    WAITING_CONFIG, // Operation đã phân công, Convenience chưa cấu hình

    CONFIGURED, // Convenience đã cấu hình xong

    NOT_STARTED, // Tour đã READY, nhưng chưa bắt đầu chạy

    IN_PROGRESS, // Tour đang chạy

    OUT_OF_STOCK, // Hết số lượng

    COMPLETED // Tour kết thúc
}