package com.project.feedback.client;
import java.util.UUID;
public record FeedbackEligibility(Long bookingId, UUID tourId, Long passengerVoyageId, boolean participated) {}
