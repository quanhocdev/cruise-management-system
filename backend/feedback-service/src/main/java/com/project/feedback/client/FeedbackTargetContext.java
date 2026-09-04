package com.project.feedback.client;

import java.util.UUID;

public record FeedbackTargetContext(UUID tourId, String targetType, UUID targetId, boolean completed) {}
