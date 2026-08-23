package com.project.tour.model.enums.convenience;

public enum ProductTourStatus {

    WAITING_CONFIG, // Operation đã phân công, ONBOARD chưa cấu hình

    NOT_STARTED, // Đã cấu hình, chờ tour chạy

    IN_PROGRESS, // Đang mở phục vụ/bán trong tour

    OUT_OF_STOCK, // Đã hết số lượng cấp cho tour này

    COMPLETED // Tour kết thúc
}