package com.project.activityvisit.model.enums;

public enum VisitTourStatus {

    WAITING_CONFIG, // Operation đã phân công, Shore chưa cấu hình

    CONFIGURED, // Shore đã cấu hình

    NOT_STARTED,

    IN_PROGRESS,

    COMPLETED,

    DELAYED,

    CANCELLED
}