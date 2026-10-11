package com.project.tour.mapper.operation;

// LƯU Ý: kiểm tra lại tên + package của DTO benefit trong TourPackageRequest.benefits()
import com.project.tour.dto.operation.packages.benefit.PackageBenefitRequest;
import com.project.tour.model.operation.PackageBenefit;

import java.util.List;
import java.util.UUID;

public class PackageBenefitMapper {

    private PackageBenefitMapper() {
    }

    public static PackageBenefit toEntity(UUID tourPackageId, PackageBenefitRequest dto) {
        PackageBenefit benefit = new PackageBenefit();
        benefit.setTourPackageId(tourPackageId);
        benefit.setType(dto.type());
        benefit.setReferenceId(dto.referenceId());
        benefit.setQuantity(dto.quantity());
        benefit.setDiscountPercent(dto.discountPercent());
        return benefit;
    }

    public static List<PackageBenefit> toEntities(UUID tourPackageId, List<PackageBenefitRequest> dtos) {
        return dtos.stream()
                .map(dto -> toEntity(tourPackageId, dto))
                .toList();
    }
}