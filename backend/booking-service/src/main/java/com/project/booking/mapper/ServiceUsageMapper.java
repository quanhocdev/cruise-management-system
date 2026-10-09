package com.project.booking.mapper;

import com.project.booking.client.ServiceTourManagementClient.ServiceUsageManagementInfo;
import com.project.booking.dto.convenience.service.ServiceUsageManagementResponse;
import com.project.booking.dto.convenience.service.ServiceUsageResponse;
import com.project.booking.model.ServiceUsage;
import org.springframework.stereotype.Component;

@Component
public class ServiceUsageMapper {

    public ServiceUsageResponse toResponse(ServiceUsage serviceUsage) {
        return new ServiceUsageResponse(
                serviceUsage.getId(),
                serviceUsage.getBookingPassenger().getId(),
                serviceUsage.getServiceTourId(),
                serviceUsage.getUnitPrice(),
                serviceUsage.getDiscountAmount(),
                serviceUsage.getFinalAmount(),
                serviceUsage.getUsedAt(),
                serviceUsage.getExpiresAt(),
                serviceUsage.getEndedAt());
    }

    public ServiceUsageManagementResponse toManagementResponse(
            ServiceUsage usage,
            ServiceUsageManagementInfo info) {

        return new ServiceUsageManagementResponse(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getServiceTourId(),
                info != null ? info.tourId() : null,
                info != null ? info.serviceId() : null,
                info != null ? info.serviceName() : null,
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                info != null ? info.serviceTourStatus() : null,
                info != null && info.serviceActive(),
                info != null ? info.maxPassengers() : null,
                info != null ? info.durationMinutes() : null,
                usage.getUsedAt(),
                usage.getExpiresAt(),
                usage.getEndedAt());
    }
}
