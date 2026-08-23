package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ProductTourAssignedEvent(
        UUID tourId,
        UUID cruiseAreaId,
        String areaType,
        String timestamp) {

    public ProductTourAssignedEvent(UUID tourId, UUID cruiseAreaId, String areaType) {
        this(tourId, cruiseAreaId, areaType, Instant.now().toString());
    }

    public ProductTourAssignedEvent(UUID tourId, UUID cruiseAreaId) {
        this(tourId, cruiseAreaId, "PRODUCT", Instant.now().toString());
    }
}