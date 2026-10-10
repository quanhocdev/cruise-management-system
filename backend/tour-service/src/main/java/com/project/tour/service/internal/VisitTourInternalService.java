
package com.project.tour.service.internal;

import com.project.tour.dto.internal.VisitTourUsageInfo;
import com.project.tour.dto.internal.VisitTourUsageManagementInfo;

import java.util.List;
import java.util.UUID;

public interface VisitTourInternalService {

    VisitTourUsageInfo getVisitTourUsageInfo(
            UUID visitTourId,
            UUID tourPackageId);

    List<VisitTourUsageManagementInfo> getVisitTourUsageManagementInfo(
            List<UUID> visitTourIds);
}
