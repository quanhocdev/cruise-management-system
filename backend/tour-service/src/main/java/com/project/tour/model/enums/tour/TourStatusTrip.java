package com.project.tour.model.enums.tour;

public enum TourStatusTrip {

    APPROVAL_PENDING, // Scheduler tạo tour, chờ Operation duyệt
    APPROVED, // Operation đã chọn tàu và duyệt
    IN_PROGRESS, // Tour đang diễn ra
    COMPLETED, // Tour đã hoàn thành
    CANCELLED // Tour bị hủy
}