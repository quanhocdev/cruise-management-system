package com.project.tour.service.internal;

import com.project.tour.dto.internal.ProductUsageInfo;
import com.project.tour.model.convenience.enums.ProductStatus;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.model.enums.tour.BenefitType;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductTourInternalServiceImpl
        implements ProductTourInternalService {

    private final ProductTourRepository productTourRepository;
    private final PackageBenefitRepository packageBenefitRepository;

    public ProductTourInternalServiceImpl(
            ProductTourRepository productTourRepository,
            PackageBenefitRepository packageBenefitRepository) {

        this.productTourRepository = productTourRepository;
        this.packageBenefitRepository = packageBenefitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductUsageInfo getProductUsageInfo(
            UUID productTourId,
            UUID tourPackageId) {

        ProductTour productTour = productTourRepository.findById(productTourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ProductTour"));

        if (productTour.getProduct() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ProductTour chưa được cấu hình Product");
        }

        PackageBenefit benefit = packageBenefitRepository
                .findAllByTourPackageId(tourPackageId)
                .stream()
                .filter(item -> item.getType() != null
                        && item.getType() == BenefitType.PRODUCT
                        && productTourId.equals(item.getReferenceId()))
                .findFirst()
                .orElse(null);

        Integer benefitQuantity = benefit != null
                && benefit.getQuantity() != null
                        ? benefit.getQuantity()
                        : 0;

        BigDecimal discountPercent = benefit != null
                && benefit.getDiscountPercent() != null
                        ? benefit.getDiscountPercent()
                        : BigDecimal.ZERO;

        return new ProductUsageInfo(
                productTour.getId(),
                productTour.getTourId(),
                productTour.getProduct().getId(),
                productTour.getProduct().getName(),
                productTour.getProduct().getPrice(),
                productTour.getStatus(),
                productTour.getProduct().getStatus() == ProductStatus.ACTIVE,
                benefit != null ? benefit.getId() : null,
                benefitQuantity,
                discountPercent);
    }
}