package com.project.tour.service.internal;

import com.project.tour.dto.internal.ServiceUsageInfo;

import java.util.UUID;

public interface ServiceTourInternalService {

    ServiceUsageInfo getServiceUsageInfo(
            UUID serviceTourId,
            UUID tourPackageId);
}