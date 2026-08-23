package com.project.tour.dto.tour;

import java.util.UUID;

public record FeedbackTargetContext(UUID tourId, String targetType, UUID targetId, boolean completed) {}
