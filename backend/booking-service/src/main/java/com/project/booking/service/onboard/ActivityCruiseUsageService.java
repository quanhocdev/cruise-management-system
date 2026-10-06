package com.project.booking.service.onboard;

import com.project.booking.dto.ActivityCruiseUsageRequest;
import com.project.booking.dto.ActivityCruiseUsageResponse;

import java.util.List;

public interface ActivityCruiseUsageService {

        ActivityCruiseUsageResponse create(
                        ActivityCruiseUsageRequest request);

        List<ActivityCruiseUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId);

        List<ActivityCruiseUsageResponse> getByUserId(Long userId);
}