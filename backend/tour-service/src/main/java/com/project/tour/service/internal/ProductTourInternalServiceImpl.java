package com.project.tour.service.internal;

import com.project.tour.dto.internal.ProductUsageInfo;
import com.project.tour.dto.internal.ProductUsageManagementInfo;
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
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

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

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageManagementInfo> getProductUsageManagementInfo(
                        List<UUID> productTourIds) {

                if (productTourIds == null || productTourIds.isEmpty()) {
                        return List.of();
                }

                return productTourRepository
                                .findAllWithProductByIdIn(productTourIds)
                                .stream()
                                .map(productTour -> {
                                        var product = productTour.getProduct();

                                        return new ProductUsageManagementInfo(
                                                        productTour.getId(),
                                                        productTour.getTourId(),
                                                        product != null ? product.getId() : null,
                                                        product != null ? product.getName() : null,
                                                        product != null ? product.getPrice() : null,
                                                        productTour.getQuantity(),
                                                        productTour.getStatus(),
                                                        product != null
                                                                        && product.getStatus() == ProductStatus.ACTIVE);
                                })
                                .toList();
        }
}