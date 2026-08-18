package com.project.tour.model.enums.tour;

public enum TourStatusTrip {

    DRAFT, // Scheduler đang cấu hình Tour
    APPROVAL_PENDING, // Scheduler hoàn tất, chờ Operation duyệt
    APPROVED, // Operation đã duyệt, các role cấu hình
    READY, // Sẵn sàng chờ đăng kí
    IN_PROGRESS, // Tour đang diễn ra
    COMPLETED, // Tour đã hoàn thành
    CANCELLED // Tour bị hủy
}