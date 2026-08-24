package com.project.common.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TourApprovedEvent(
        UUID tourId,
        List<TourAssignmentEvent> assignments,
        String timestamp) {

    public TourApprovedEvent(
            UUID tourId,
            List<TourAssignmentEvent> assignments) {

        this(
                tourId,
                assignments,
                Instant.now().toString());
    }
}