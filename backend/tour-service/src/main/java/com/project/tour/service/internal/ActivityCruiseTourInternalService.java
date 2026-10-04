package com.project.tour.service.internal;

import com.project.tour.dto.internal.ActivityCruiseUsageInfo;

import java.util.UUID;

public interface ActivityCruiseTourInternalService {

    ActivityCruiseUsageInfo getActivityCruiseUsageInfo(
            UUID activityCruiseTourId,
            UUID tourPackageId);
}