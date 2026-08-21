package com.project.feedback.client;
import java.util.UUID;
public record FeedbackTourContext(UUID tourId, UUID cruiseId, boolean completed) {}
