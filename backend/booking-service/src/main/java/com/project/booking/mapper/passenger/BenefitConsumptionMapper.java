
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.benefit.PassengerBenefitConsumptionDTO;
import com.project.booking.dto.passenger.benefit.PassengerBenefitDTO;
import com.project.booking.model.BenefitConsumption;

import java.util.List;

public class BenefitConsumptionMapper {

    public PassengerBenefitConsumptionDTO toDTO(
            BenefitConsumption consumption) {

        if (consumption == null) {
            return null;
        }

        return new PassengerBenefitConsumptionDTO(
                consumption.getId(),
                consumption.getBookingId(),
                consumption.getPackageBenefitId(),
                consumption.getUsedQuantity(),
                consumption.getCreatedAt(),
                consumption.getUpdatedAt());
    }

    public PassengerBenefitDTO toBenefitDTO(
            Long bookingId,
            List<BenefitConsumption> consumptions) {

        return new PassengerBenefitDTO(
                bookingId,
                consumptions.stream()
                        .map(this::toDTO)
                        .toList());
    }
}
