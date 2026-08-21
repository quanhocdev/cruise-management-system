// src/main/java/com/project/tour/dto/tour/operation/OperationTourConfigurationResponse.java

package com.project.tour.dto.tour.operation;

import java.util.List;
import java.util.UUID;

public record OperationTourConfigurationResponse(

        UUID tourId,
        String tourCode,
        String tourName,

        List<ActivityCruiseTourAssignmentResponse> activities,

        List<ProductTourAssignmentResponse> products,

        List<ServiceTourAssignmentResponse> services,

        boolean configurationComplete

) {
}