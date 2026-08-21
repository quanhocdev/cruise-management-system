package com.project.booking.dto;

import java.util.UUID;

public record FeedbackEligibilityResponse(
    Long bookingId,
    UUID tourId,
    Long passengerVoyageId,
    boolean participated
) {}
