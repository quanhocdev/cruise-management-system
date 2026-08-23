package com.project.activitycruise.model.enums;

public enum ActivityCruiseTourStatus {

    WAITING_CONFIG, // Operation đã phân công, ONBOARD chưa cấu hình

    NOT_STARTED, // Đã cấu hình, chưa diễn ra

    IN_PROGRESS, // Đang diễn ra

    COMPLETED // Đã kết thúc
}