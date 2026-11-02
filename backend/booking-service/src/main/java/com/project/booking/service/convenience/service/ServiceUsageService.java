package com.project.booking.service.convenience.service;

import java.util.UUID;

import com.project.booking.dto.convenience.service.ServiceUsageManagementResponse;
import com.project.booking.dto.convenience.service.ServiceUsageRequest;
import com.project.booking.dto.convenience.service.ServiceUsageResponse;

import java.util.List;

public interface ServiceUsageService {

        /** Quẹt NFC: đang dùng → checkout, chưa dùng → check-in. */
        ServiceUsageResponse scan(ServiceUsageRequest request);

        List<ServiceUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId);

        List<ServiceUsageResponse> getByUserId(UUID userId);

        List<ServiceUsageManagementResponse> getManagementUsages();
}
