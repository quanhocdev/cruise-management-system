package com.project.tour.dto.tour;

import java.util.UUID;

public record FeedbackTourContext(UUID tourId, UUID cruiseId, boolean completed) {}
