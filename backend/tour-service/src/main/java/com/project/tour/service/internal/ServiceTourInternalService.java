package com.project.tour.service.internal;

import com.project.tour.dto.internal.ServiceUsageInfo;
import com.project.tour.dto.internal.ServiceUsageManagementInfo;
import java.util.List;
import java.util.UUID;

public interface ServiceTourInternalService {

    ServiceUsageInfo getServiceUsageInfo(
            UUID serviceTourId,
            UUID tourPackageId);

    List<ServiceUsageManagementInfo> getServiceUsageManagementInfo(
            List<UUID> serviceTourIds);
}