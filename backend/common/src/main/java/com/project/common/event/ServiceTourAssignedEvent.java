package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ServiceTourAssignedEvent(
        UUID tourId,
        UUID cruiseAreaId,
        String areaType,
        String action, // CREATE, UPDATE, DELETE
        String timestamp) {

    // Constructor mở rộng nhận hành động cụ thể (CREATE, UPDATE, DELETE)
    public ServiceTourAssignedEvent(UUID tourId, UUID cruiseAreaId, String areaType, String action) {
        this(tourId, cruiseAreaId, areaType, action, Instant.now().toString());
    }

    // Constructor khi gán/tạo mới (mặc định action = CREATE)
    public ServiceTourAssignedEvent(UUID tourId, UUID cruiseAreaId, String areaType) {
        this(tourId, cruiseAreaId, areaType, "CREATE", Instant.now().toString());
    }

    // Constructor tiện lợi mặc định "SERVICE" và "CREATE"
    public ServiceTourAssignedEvent(UUID tourId, UUID cruiseAreaId) {
        this(tourId, cruiseAreaId, "SERVICE", "CREATE", Instant.now().toString());
    }
}