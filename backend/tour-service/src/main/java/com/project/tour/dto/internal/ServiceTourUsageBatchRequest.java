package com.project.tour.dto.internal;

import java.util.List;
import java.util.UUID;

public record ServiceTourUsageBatchRequest(
        List<UUID> serviceTourIds) {
}