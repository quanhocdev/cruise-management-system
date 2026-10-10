package com.project.tour.service.internal;

import com.project.tour.dto.internal.ActivityCruiseUsageInfo;
import com.project.tour.dto.internal.ActivityCruiseUsageManagementInfo;

import java.util.List;
import java.util.UUID;

public interface ActivityCruiseTourInternalService {

    ActivityCruiseUsageInfo getActivityCruiseUsageInfo(
            UUID activityCruiseTourId,
            UUID tourPackageId);

    List<ActivityCruiseUsageManagementInfo> getActivityCruiseUsageManagementInfo(
            List<UUID> activityCruiseTourIds);
}