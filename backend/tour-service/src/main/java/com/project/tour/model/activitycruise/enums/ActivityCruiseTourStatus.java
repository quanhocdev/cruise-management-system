package com.project.tour.model.activitycruise.enums;

public enum ActivityCruiseTourStatus {

    WAITING_CONFIG, // Operation đã phân công, Onboard chưa cấu hình

    CONFIGURED, // Đã cấu hình, chờ Tour chuyển sang READY

    NOT_STARTED, // Tour READY, chưa diễn ra

    IN_PROGRESS, // Đang diễn ra

    COMPLETED // Đã kết thúc
}
