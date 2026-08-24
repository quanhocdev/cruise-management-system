package com.project.common.event;

import java.util.UUID;

public record TourAssignmentEvent(
        UUID tourId,
        UUID cruiseAreaId,
        String areaType) {
}