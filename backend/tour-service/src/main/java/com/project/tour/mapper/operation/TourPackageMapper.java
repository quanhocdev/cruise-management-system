package com.project.tour.mapper.operation;

import com.project.tour.dto.operation.packages.TourPackageResponse;
import com.project.tour.dto.operation.packages.benefit.PackageBenefitResponse;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.model.operation.TourPackage;

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
                benefit.getQuantity(),
                benefit.getDiscountPercent());
    }

    /**
     * @param roomCapacity capacity của RoomType, do caller lấy từ DB (không có giá
     *                     trị mặc định)
     */
    public static TourPackageResponse toResponse(
            TourPackage tourPackage,
            List<PackageBenefit> benefits,
            Integer roomCapacity) {

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
                roomCapacity,
                tourPackage.getStatus(),
                benefitResponses,
                tourPackage.getCreatedAt(),
                tourPackage.getUpdatedAt());
    }
}