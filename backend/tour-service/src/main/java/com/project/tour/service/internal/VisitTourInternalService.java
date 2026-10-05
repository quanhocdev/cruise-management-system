package com.project.tour.service.internal;

import com.project.tour.dto.internal.VisitTourUsageInfo;

import java.util.UUID;

public interface VisitTourInternalService {

    VisitTourUsageInfo getVisitTourUsageInfo(
            UUID visitTourId,
            UUID tourPackageId);
}