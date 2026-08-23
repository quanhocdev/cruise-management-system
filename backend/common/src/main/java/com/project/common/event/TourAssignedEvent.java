package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record TourAssignedEvent(
        UUID tourId,
        UUID cruiseAreaId,
        String areaType, // "PRODUCT", "SERVICE", "ACTIVITY"
        String action, // "CREATE", "UPDATE", "DELETE"
        String timestamp) {

    public TourAssignedEvent(UUID tourId, UUID cruiseAreaId, String areaType, String action) {
        this(tourId, cruiseAreaId, areaType, action, Instant.now().toString());
    }

    public TourAssignedEvent(UUID tourId, UUID cruiseAreaId, String areaType) {
        this(tourId, cruiseAreaId, areaType, "CREATE", Instant.now().toString());
    }
}