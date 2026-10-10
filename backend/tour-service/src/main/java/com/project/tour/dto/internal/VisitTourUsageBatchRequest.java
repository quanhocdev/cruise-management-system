
package com.project.tour.dto.internal;

import java.util.List;
import java.util.UUID;

public record VisitTourUsageBatchRequest(
        List<UUID> visitTourIds) {
}
