package com.project.feedback.dto;

import java.util.List;
import java.util.UUID;

public record FeedbackOptions(Long bookingId, UUID tourId, String state, List<Guest> passengers) {
    public record Guest(Long bookingPassengerId, String name, boolean participated) {}
}
