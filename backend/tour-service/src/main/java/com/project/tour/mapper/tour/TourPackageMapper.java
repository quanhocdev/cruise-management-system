package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.packages.PackageBenefitResponse;
import com.project.tour.dto.tour.packages.TourPackageResponse;
import com.project.tour.model.PackageBenefit;
import com.project.tour.model.TourPackage;

import java.util.List;

public class TourPackageMapper {

    public static PackageBenefitResponse toBenefitResponse(PackageBenefit benefit) {
        if (benefit == null) {
            return null;
        }
        return new PackageBenefitResponse(
                benefit.getId(),
                benefit.getTourPackageId(),
                benefit.getType(),
                benefit.getReferenceId(),
                benefit.getFreeQuantity(),
                benefit.getDiscountPercent());
    }

    public static TourPackageResponse toResponse(TourPackage tourPackage, List<PackageBenefit> benefits) {
        if (tourPackage == null) {
            return null;
        }

        List<PackageBenefitResponse> benefitResponses = benefits != null
                ? benefits.stream().map(TourPackageMapper::toBenefitResponse).toList()
                : List.of();

        return new TourPackageResponse(
                tourPackage.getId(),
                tourPackage.getTourId(),
                tourPackage.getRoomTypeId(),
                tourPackage.getName(),
                tourPackage.getDescription(),
                tourPackage.getPrice(),
                tourPackage.getMaxPassengers(),
                tourPackage.getStatus(),
                benefitResponses,
                tourPackage.getCreatedAt(),
                tourPackage.getUpdatedAt());
    }
}