package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.tourpackage.PassengerPackageBenefitDTO;
import com.project.tour.dto.passenger.tourpackage.PassengerTourPackageDTO;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.model.operation.TourPackage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TourPackageMapper {

    public PassengerPackageBenefitDTO toBenefitDTO(
            PackageBenefit benefit,
            String itemName) {
        if (benefit == null) {
            return null;
        }

        return new PassengerPackageBenefitDTO(
                benefit.getId(),
                benefit.getType(),
                benefit.getReferenceId(),
                itemName,
                benefit.getQuantity(),
                benefit.getDiscountPercent());
    }

    public List<PassengerPackageBenefitDTO> toBenefitDTOs(
            List<PackageBenefit> benefits,
            Map<UUID, String> itemNames) {
        if (benefits == null) {
            return List.of();
        }

        Map<UUID, String> names = itemNames != null
                ? itemNames
                : Map.of();

        return benefits.stream()
                .map(benefit -> toBenefitDTO(
                        benefit,
                        names.get(benefit.getReferenceId())))
                .toList();
    }

    public PassengerTourPackageDTO toDTO(
            TourPackage tourPackage,
            List<PackageBenefit> benefits,
            Map<UUID, String> itemNames) {
        if (tourPackage == null) {
            return null;
        }

        var roomType = tourPackage.getRoomType();

        return new PassengerTourPackageDTO(
                tourPackage.getId(),
                tourPackage.getTourId(),
                tourPackage.getRoomTypeId(),
                roomType != null ? roomType.getName() : null,
                tourPackage.getName(),
                tourPackage.getDescription(),
                tourPackage.getPrice(),
                toBenefitDTOs(benefits, itemNames));
    }
}