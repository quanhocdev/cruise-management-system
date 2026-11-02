package com.project.feedback.client;
import java.util.UUID;
public record FeedbackEligibility(Long bookingId, UUID tourId, Long passengerVoyageId,
                                  boolean participated, Long bookingPassengerId) {
    public FeedbackEligibility(Long bookingId, UUID tourId, Long passengerVoyageId, boolean participated) {
        this(bookingId, tourId, passengerVoyageId, participated, null);
    }
}
