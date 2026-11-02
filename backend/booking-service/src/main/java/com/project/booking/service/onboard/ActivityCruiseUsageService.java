package com.project.booking.service.onboard;

import java.util.UUID;

import com.project.booking.dto.onboard.ActivityCruiseUsageManagementResponse;
import com.project.booking.dto.onboard.ActivityCruiseUsageRequest;
import com.project.booking.dto.onboard.ActivityCruiseUsageResponse;

import java.util.List;

public interface ActivityCruiseUsageService {
        ActivityCruiseUsageResponse createForPassenger(Long passengerId, java.util.UUID targetId, java.util.UUID requestId);

        ActivityCruiseUsageResponse create(
                        ActivityCruiseUsageRequest request);

        List<ActivityCruiseUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId);

        List<ActivityCruiseUsageResponse> getByUserId(UUID userId);

        List<ActivityCruiseUsageManagementResponse> getManagementUsages();
}
