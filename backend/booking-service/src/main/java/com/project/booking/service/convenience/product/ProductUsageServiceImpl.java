
package com.project.booking.service.convenience.product;

import com.project.booking.client.ProductTourManagementClient;
import com.project.booking.client.ProductTourManagementClient.ProductUsageManagementInfo;
import com.project.booking.client.TourProductClient;
import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.kafka.ProductUsedEventProducer;
import com.project.booking.mapper.ProductUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ProductUsage;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.service.common.benefit.BenefitConsumptionService;
import com.project.booking.service.common.pricing.PricingCalculator;
import com.project.common.event.ProductUsedEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductUsageServiceImpl implements ProductUsageService {

        private final ProductUsageRepository productUsageRepository;
        private final ProductUsageMapper productUsageMapper;
        private final TourProductClient tourProductClient;
        private final BenefitConsumptionService benefitConsumptionService;
        private final ProductUsedEventProducer productUsedEventProducer;
        private final ProductValidator productValidator;
        private final PricingCalculator pricingCalculator;
        private final ProductTourManagementClient productTourManagementClient;

        public ProductUsageServiceImpl(
                        ProductUsageRepository productUsageRepository,
                        ProductUsageMapper productUsageMapper,
                        TourProductClient tourProductClient,
                        BenefitConsumptionService benefitConsumptionService,
                        ProductUsedEventProducer productUsedEventProducer,
                        ProductValidator productValidator,
                        PricingCalculator pricingCalculator,
                        ProductTourManagementClient productTourManagementClient) {

                this.productUsageRepository = productUsageRepository;
                this.productUsageMapper = productUsageMapper;
                this.tourProductClient = tourProductClient;
                this.benefitConsumptionService = benefitConsumptionService;
                this.productUsedEventProducer = productUsedEventProducer;
                this.productValidator = productValidator;
                this.pricingCalculator = pricingCalculator;
                this.productTourManagementClient = productTourManagementClient;
        }

        @Override
        @Transactional
        public ProductUsageResponse create(ProductUsageRequest request) {

                // 1. Validate hành khách bằng NFC + booking
                BookingPassenger bookingPassenger = productValidator
                                .validateAndGetPassenger(request.nfcCardUid());

                Booking booking = bookingPassenger.getBooking();
                productValidator.validateBooking(booking);

                // 2. Lấy thông tin ProductTour từ Tour Service
                TourProductClient.ProductUsageInfo productInfo = tourProductClient.getProductUsageInfo(
                                request.productTourId(),
                                booking.getTourPackageId());

                // 3. Validate nghiệp vụ sản phẩm
                productValidator.validateProduct(booking, productInfo);

                // 4. Lấy thông tin benefit
                UUID packageBenefitId = productInfo.packageBenefitId();

                int benefitQuantity = productInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(productInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                // 5. Lock BenefitConsumption để tránh sử dụng benefit đồng thời
                if (packageBenefitId != null && benefitQuantity > 0) {

                        benefitConsumption = benefitConsumptionService.getForUpdate(
                                        booking.getId(),
                                        packageBenefitId);

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 6. Tính giá bằng PricingCalculator dùng chung
                var pricingResult = pricingCalculator.calculate(
                                productInfo.unitPrice(),
                                productInfo.discountPercent(),
                                request.quantity(),
                                benefitQuantity,
                                usedBenefitQuantity);

                // 7. Cập nhật số lượng benefit đã được áp dụng
                if (benefitConsumption != null
                                && pricingResult.discountedQuantity() > 0) {

                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        pricingResult.discountedQuantity());
                }

                // 8. Lưu ProductUsage với snapshot giá tại thời điểm sử dụng
                ProductUsage usage = new ProductUsage();
                usage.setBookingPassenger(bookingPassenger);
                usage.setProductTourId(request.productTourId());
                usage.setQuantity(request.quantity());
                usage.setUnitPrice(pricingResult.unitPrice());
                usage.setDiscountAmount(pricingResult.discountAmount());
                usage.setFinalAmount(pricingResult.finalAmount());
                usage.setUsedAt(LocalDateTime.now());

                ProductUsage savedUsage = productUsageRepository.save(usage);

                // 9. Gửi event sau khi lưu usage
                productUsedEventProducer.send(
                                new ProductUsedEvent(
                                                booking.getId(),
                                                bookingPassenger.getId(),
                                                request.productTourId(),
                                                request.quantity(),
                                                Instant.now()));

                return productUsageMapper.toResponse(savedUsage);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId) {

                return productUsageRepository
                                .findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId)
                                .stream()
                                .map(productUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageResponse> getByUserId(Long userId) {

                return productUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(userId)
                                .stream()
                                .map(productUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageManagementResponse> getManagementUsages() {

                List<ProductUsage> usages = productUsageRepository
                                .findAllByOrderByUsedAtDesc();

                if (usages.isEmpty()) {
                        return List.of();
                }

                List<UUID> productTourIds = usages.stream()
                                .map(ProductUsage::getProductTourId)
                                .filter(java.util.Objects::nonNull)
                                .distinct()
                                .toList();

                List<ProductUsageManagementInfo> productInfos = productTourManagementClient
                                .getProductUsageInfo(productTourIds);

                Map<UUID, ProductUsageManagementInfo> productInfoMap = productInfos.stream()
                                .collect(Collectors.toMap(
                                                ProductUsageManagementInfo::productTourId,
                                                Function.identity(),
                                                (first, second) -> first));

                return usages.stream()
                                .map(usage -> productUsageMapper.toManagementResponse(
                                                usage,
                                                productInfoMap.get(usage.getProductTourId())))
                                .toList();
        }
}
