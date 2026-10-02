package com.project.booking.mapper;

import com.project.booking.dto.ServiceUsageResponse;
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
}