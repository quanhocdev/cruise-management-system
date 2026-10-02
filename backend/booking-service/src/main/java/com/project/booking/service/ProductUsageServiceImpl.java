package com.project.booking.service;

import com.project.booking.client.TourProductClient;
import com.project.booking.dto.ProductUsageRequest;
import com.project.booking.dto.ProductUsageResponse;
import com.project.booking.kafka.ProductUsedEventProducer;
import com.project.booking.mapper.ProductUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ProductUsage;
import com.project.booking.repository.BenefitConsumptionRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.common.event.ProductUsedEvent;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductUsageServiceImpl implements ProductUsageService {

        private final ProductUsageRepository productUsageRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final ProductUsageMapper productUsageMapper;
        private final TourProductClient tourProductClient;
        private final BenefitConsumptionRepository benefitConsumptionRepository;
        private final ProductUsedEventProducer productUsedEventProducer;

        public ProductUsageServiceImpl(
                        ProductUsageRepository productUsageRepository,
                        BookingPassengerRepository bookingPassengerRepository,
                        ProductUsageMapper productUsageMapper,
                        TourProductClient tourProductClient,
                        BenefitConsumptionRepository benefitConsumptionRepository,
                        ProductUsedEventProducer productUsedEventProducer) {

                this.productUsageRepository = productUsageRepository;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.productUsageMapper = productUsageMapper;
                this.tourProductClient = tourProductClient;
                this.benefitConsumptionRepository = benefitConsumptionRepository;
                this.productUsedEventProducer = productUsedEventProducer;
        }

        @Override
        @Transactional
        public ProductUsageResponse create(ProductUsageRequest request) {

                // 1. Tìm BookingPassenger bằng NFC
                BookingPassenger bookingPassenger = bookingPassengerRepository
                                .findByNfcCardUid(request.nfcCardUid())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Không tìm thấy hành khách với NFC card UID: "
                                                                + request.nfcCardUid()));

                Booking booking = bookingPassenger.getBooking();

                if (booking == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "BookingPassenger không thuộc booking nào");
                }

                if (booking.getTourId() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Booking không có tour");
                }

                if (booking.getTourPackageId() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Booking không có tour package");
                }

                // 2. Gọi Tour Service lấy thông tin ProductTour
                TourProductClient.ProductUsageInfo productInfo = tourProductClient.getProductUsageInfo(
                                request.productTourId(),
                                booking.getTourPackageId());

                if (productInfo == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy thông tin ProductTour");
                }

                // 3. Kiểm tra ProductTour thuộc đúng Tour
                if (!booking.getTourId().equals(productInfo.tourId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Product không thuộc tour của booking");
                }

                // 4. Kiểm tra Product còn ACTIVE
                if (!productInfo.productActive()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Product hiện không còn hoạt động");
                }

                // 5. Kiểm tra ProductTour status
                validateProductTourStatus(
                                productInfo.productTourStatus());

                // 6. Kiểm tra giá
                if (productInfo.unitPrice() == null
                                || productInfo.unitPrice().compareTo(BigDecimal.ZERO) < 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Giá Product không hợp lệ");
                }

                // 7. Lấy thông tin PackageBenefit
                UUID packageBenefitId = productInfo.packageBenefitId();

                int benefitQuantity = productInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(productInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;

                long usedBenefitQuantity = 0;

                if (packageBenefitId != null && benefitQuantity > 0) {

                        // Đảm bảo BenefitConsumption tồn tại
                        benefitConsumptionRepository.createIfNotExists(
                                        booking.getId(),
                                        packageBenefitId);

                        // Lock row để chống race condition
                        benefitConsumption = benefitConsumptionRepository
                                        .findForUpdate(
                                                        booking.getId(),
                                                        packageBenefitId)
                                        .orElseThrow(() -> new ResponseStatusException(
                                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Không tìm thấy BenefitConsumption"));

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 8. Tính số lượng được miễn phí trong lần sử dụng này
                long remainingFreeQuantity = Math.max(
                                (long) benefitQuantity - usedBenefitQuantity,
                                0);

                long freeQuantityThisUsage = Math.min(
                                remainingFreeQuantity,
                                request.quantity());

                long paidQuantityThisUsage = request.quantity() - freeQuantityThisUsage;

                // 9. Tính tổng tiền trước discount
                BigDecimal unitPrice = productInfo.unitPrice()
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal grossAmount = unitPrice.multiply(
                                BigDecimal.valueOf(request.quantity()));

                // 10. Discount từ benefit quantity
                BigDecimal freeDiscountAmount = unitPrice.multiply(
                                BigDecimal.valueOf(freeQuantityThisUsage));

                // 11. Discount percent
                BigDecimal discountPercent = productInfo.discountPercent() == null
                                ? BigDecimal.ZERO
                                : productInfo.discountPercent();

                if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Discount percent không hợp lệ");
                }

                BigDecimal paidAmountBeforePercent = unitPrice.multiply(
                                BigDecimal.valueOf(paidQuantityThisUsage));

                BigDecimal percentDiscountAmount = paidAmountBeforePercent
                                .multiply(discountPercent)
                                .divide(
                                                BigDecimal.valueOf(100),
                                                2,
                                                RoundingMode.HALF_UP);

                // 12. Tổng discount
                BigDecimal discountAmount = freeDiscountAmount
                                .add(percentDiscountAmount)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                // 13. Final amount
                BigDecimal finalAmount = grossAmount
                                .subtract(discountAmount)
                                .max(BigDecimal.ZERO)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                // 14. Cập nhật BenefitConsumption
                if (benefitConsumption != null
                                && freeQuantityThisUsage > 0) {

                        int currentUsedQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();

                        benefitConsumption.setUsedQuantity(
                                        currentUsedQuantity
                                                        + (int) freeQuantityThisUsage);

                        benefitConsumptionRepository.save(
                                        benefitConsumption);
                }

                // 15. Tạo ProductUsage
                ProductUsage usage = new ProductUsage();

                usage.setBookingPassenger(bookingPassenger);
                usage.setProductTourId(request.productTourId());
                usage.setQuantity(request.quantity());

                // Snapshot giá tại thời điểm sử dụng
                usage.setUnitPrice(unitPrice);

                usage.setDiscountAmount(discountAmount);
                usage.setFinalAmount(finalAmount);
                usage.setUsedAt(LocalDateTime.now());

                // 16. Save ProductUsage
                ProductUsage savedUsage = productUsageRepository.save(usage);

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
                                .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                                                bookingPassengerId)
                                .stream()
                                .map(productUsageMapper::toResponse)
                                .toList();
        }

        // Validate ProductTour status
        private void validateProductTourStatus(String status) {

                if (status == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "ProductTour chưa có trạng thái");
                }

                switch (status) {

                        case "CONFIGURED":
                        case "NOT_STARTED":
                        case "IN_PROGRESS":
                                return;

                        case "WAITING_CONFIG":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "ProductTour chưa được cấu hình");

                        case "OUT_OF_STOCK":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Product đã hết hàng");

                        case "COMPLETED":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "ProductTour đã hoàn thành");

                        default:
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Trạng thái ProductTour không hợp lệ: "
                                                                + status);
                }
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