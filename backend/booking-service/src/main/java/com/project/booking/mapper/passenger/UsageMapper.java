
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.usage.PassengerActivityCruiseUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerActivityVisitUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerProductUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerServiceUsageDTO;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;

import java.util.List;

public class UsageMapper {

    public PassengerProductUsageDTO toProductUsageDTO(ProductUsage usage) {
        if (usage == null) {
            return null;
        }

        return new PassengerProductUsageDTO(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getProductTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt());
    }

    public PassengerServiceUsageDTO toServiceUsageDTO(ServiceUsage usage) {
        if (usage == null) {
            return null;
        }

        return new PassengerServiceUsageDTO(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getServiceTourId(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt(),
                usage.getExpiresAt(),
                usage.getEndedAt());
    }

    public PassengerActivityCruiseUsageDTO toActivityCruiseUsageDTO(
            ActivityCruiseUsage usage) {
        if (usage == null) {
            return null;
        }

        return new PassengerActivityCruiseUsageDTO(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getActivityCruiseTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt(),
                usage.getEndedAt());
    }

    public PassengerActivityVisitUsageDTO toActivityVisitUsageDTO(
            ActivityVisitUsage usage) {
        if (usage == null) {
            return null;
        }

        return new PassengerActivityVisitUsageDTO(
                usage.getId(),
                usage.getBookingPassenger().getId(),
                usage.getVisitTourId(),
                usage.getQuantity(),
                usage.getUnitPrice(),
                usage.getDiscountAmount(),
                usage.getFinalAmount(),
                usage.getUsedAt(),
                usage.getEndedAt());
    }

    public List<PassengerProductUsageDTO> toProductUsageDTOs(
            List<ProductUsage> usages) {
        return usages.stream()
                .map(this::toProductUsageDTO)
                .toList();
    }

    public List<PassengerServiceUsageDTO> toServiceUsageDTOs(
            List<ServiceUsage> usages) {
        return usages.stream()
                .map(this::toServiceUsageDTO)
                .toList();
    }

    public List<PassengerActivityCruiseUsageDTO> toActivityCruiseUsageDTOs(
            List<ActivityCruiseUsage> usages) {
        return usages.stream()
                .map(this::toActivityCruiseUsageDTO)
                .toList();
    }

    public List<PassengerActivityVisitUsageDTO> toActivityVisitUsageDTOs(
            List<ActivityVisitUsage> usages) {
        return usages.stream()
                .map(this::toActivityVisitUsageDTO)
                .toList();
    }
}
