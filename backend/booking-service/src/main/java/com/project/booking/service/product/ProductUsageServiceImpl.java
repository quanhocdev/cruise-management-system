package com.project.booking.service.product;

import com.project.booking.client.TourProductClient;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.kafka.ProductUsedEventProducer;
import com.project.booking.mapper.ProductUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ProductUsage;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.service.BenefitConsumptionService;
import com.project.common.event.ProductUsedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductUsageServiceImpl implements ProductUsageService {

        private final ProductUsageRepository productUsageRepository;
        private final ProductUsageMapper productUsageMapper;
        private final TourProductClient tourProductClient;
        private final BenefitConsumptionService benefitConsumptionService;
        private final ProductUsedEventProducer productUsedEventProducer;
        private final ProductValidator productValidator;
        private final ProductPricingCalculator productPricingCalculator;

        public ProductUsageServiceImpl(
                        ProductUsageRepository productUsageRepository,
                        ProductUsageMapper productUsageMapper,
                        TourProductClient tourProductClient,
                        BenefitConsumptionService benefitConsumptionService,
                        ProductUsedEventProducer productUsedEventProducer,
                        ProductValidator productValidator,
                        ProductPricingCalculator productPricingCalculator) {

                this.productUsageRepository = productUsageRepository;
                this.productUsageMapper = productUsageMapper;
                this.tourProductClient = tourProductClient;
                this.benefitConsumptionService = benefitConsumptionService;
                this.productUsedEventProducer = productUsedEventProducer;
                this.productValidator = productValidator;
                this.productPricingCalculator = productPricingCalculator;
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

                // 3 -> 6. Validate nghiệp vụ (tour, active, status, giá)
                productValidator.validateProduct(booking, productInfo);

                // 7. Benefit + lock row
                UUID packageBenefitId = productInfo.packageBenefitId();
                int benefitQuantity = productInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(productInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                if (packageBenefitId != null && benefitQuantity > 0) {
                        benefitConsumption = benefitConsumptionService.getForUpdate(
                                        booking.getId(),
                                        packageBenefitId);

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 8 -> 13. Tính tiền
                var pricingResult = productPricingCalculator.calculate(
                                productInfo,
                                usedBenefitQuantity,
                                request.quantity());

                // 14. Cập nhật lượt benefit đã dùng
                if (benefitConsumption != null && pricingResult.freeQuantityThisUsage() > 0) {
                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        pricingResult.freeQuantityThisUsage());
                }

                // 15 & 16. Lưu usage (snapshot giá tại thời điểm sử dụng)
                ProductUsage usage = new ProductUsage();
                usage.setBookingPassenger(bookingPassenger);
                usage.setProductTourId(request.productTourId());
                usage.setQuantity(request.quantity());
                usage.setUnitPrice(pricingResult.unitPrice());
                usage.setDiscountAmount(pricingResult.discountAmount());
                usage.setFinalAmount(pricingResult.finalAmount());
                usage.setUsedAt(LocalDateTime.now());

                ProductUsage savedUsage = productUsageRepository.save(usage);

                // 17. Bắn event
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
        public List<ProductUsageResponse> getByBookingPassengerId(Long bookingPassengerId) {
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
}