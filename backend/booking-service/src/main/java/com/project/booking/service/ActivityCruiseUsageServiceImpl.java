package com.project.booking.service;

import com.project.booking.client.ActivityCruiseClient;
import com.project.booking.dto.ActivityCruiseUsageRequest;
import com.project.booking.dto.ActivityCruiseUsageResponse;
import com.project.booking.mapper.ActivityCruiseUsageMapper;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.BenefitConsumptionRepository;
import com.project.booking.repository.BookingPassengerRepository;
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
public class ActivityCruiseUsageServiceImpl
                implements ActivityCruiseUsageService {

        private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
        private final ActivityCruiseUsageMapper activityCruiseUsageMapper;
        private final ActivityCruiseClient activityCruiseClient;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final BenefitConsumptionRepository benefitConsumptionRepository;

        public ActivityCruiseUsageServiceImpl(
                        ActivityCruiseUsageRepository activityCruiseUsageRepository,
                        ActivityCruiseUsageMapper activityCruiseUsageMapper,
                        ActivityCruiseClient activityCruiseClient,
                        BookingPassengerRepository bookingPassengerRepository,
                        BenefitConsumptionRepository benefitConsumptionRepository) {

                this.activityCruiseUsageRepository = activityCruiseUsageRepository;

                this.activityCruiseUsageMapper = activityCruiseUsageMapper;

                this.activityCruiseClient = activityCruiseClient;

                this.bookingPassengerRepository = bookingPassengerRepository;

                this.benefitConsumptionRepository = benefitConsumptionRepository;
        }

        @Override
        @Transactional
        public ActivityCruiseUsageResponse create(
                        ActivityCruiseUsageRequest request) {

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

                // 2. Gọi Tour Service lấy thông tin ActivityCruiseTour
                ActivityCruiseClient.ActivityCruiseUsageInfo activityInfo = activityCruiseClient
                                .getActivityCruiseUsageInfo(
                                                request.activityCruiseTourId(),
                                                booking.getTourPackageId());

                if (activityInfo == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy thông tin ActivityCruiseTour");
                }

                // 3. Kiểm tra ActivityCruiseTour thuộc đúng Tour
                if (!booking.getTourId().equals(activityInfo.tourId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise không thuộc tour của booking");
                }

                // 4. Kiểm tra ActivityCruise còn ACTIVE
                if (!activityInfo.activityActive()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise hiện không còn hoạt động");
                }

                // 5. Kiểm tra ActivityCruiseTour status
                validateActivityCruiseTourStatus(
                                activityInfo.activityCruiseTourStatus());

                // 6. Kiểm tra thời gian hoạt động
                LocalDateTime now = LocalDateTime.now();

                if (activityInfo.startTime() == null
                                || activityInfo.endTime() == null) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise chưa được cấu hình thời gian");
                }

                if (now.isBefore(activityInfo.startTime())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise chưa bắt đầu");
                }

                if (now.isAfter(activityInfo.endTime())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise đã kết thúc");
                }

                // 7. Kiểm tra max passengers
                if (activityInfo.maxPassengers() == null
                                || activityInfo.maxPassengers() <= 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Số lượng hành khách tối đa của Activity Cruise không hợp lệ");
                }

                long currentPassengerCount = activityCruiseUsageRepository
                                .countByActivityCruiseTourId(
                                                request.activityCruiseTourId());

                if (currentPassengerCount >= activityInfo.maxPassengers()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Activity Cruise đã đủ số lượng hành khách");
                }

                // Activity Cruise luôn sử dụng 1 suất
                int quantity = 1;

                // 8. Kiểm tra giá
                if (activityInfo.price() == null
                                || activityInfo.price().compareTo(BigDecimal.ZERO) < 0) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Giá Activity Cruise không hợp lệ");
                }

                // 9. Lấy thông tin PackageBenefit
                UUID packageBenefitId = activityInfo.packageBenefitId();

                int benefitQuantity = activityInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(activityInfo.benefitQuantity(), 0);

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

                // 10. Tính số lượng được miễn phí
                long remainingFreeQuantity = Math.max(
                                (long) benefitQuantity - usedBenefitQuantity,
                                0);

                long freeQuantityThisUsage = Math.min(
                                remainingFreeQuantity,
                                quantity);

                long paidQuantityThisUsage = quantity - freeQuantityThisUsage;

                // 11. Tính tổng tiền trước discount
                BigDecimal unitPrice = activityInfo.price()
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal grossAmount = unitPrice.multiply(
                                BigDecimal.valueOf(quantity));

                // 12. Discount từ benefit quantity
                BigDecimal freeDiscountAmount = unitPrice.multiply(
                                BigDecimal.valueOf(freeQuantityThisUsage));

                // 13. Discount percent
                BigDecimal discountPercent = activityInfo.discountPercent() == null
                                ? BigDecimal.ZERO
                                : activityInfo.discountPercent();

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

                // 14. Tổng discount
                BigDecimal discountAmount = freeDiscountAmount
                                .add(percentDiscountAmount)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                // 15. Final amount
                BigDecimal finalAmount = grossAmount
                                .subtract(discountAmount)
                                .max(BigDecimal.ZERO)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                // 16. Cập nhật BenefitConsumption
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

                // 17. Tạo ActivityCruiseUsage
                ActivityCruiseUsage usage = new ActivityCruiseUsage();

                usage.setBookingPassenger(
                                bookingPassenger);

                usage.setActivityCruiseTourId(
                                request.activityCruiseTourId());

                // Activity Cruise luôn là 1 suất / 1 hành khách
                usage.setQuantity(quantity);

                // Snapshot giá tại thời điểm sử dụng
                usage.setUnitPrice(unitPrice);

                usage.setDiscountAmount(discountAmount);

                usage.setFinalAmount(finalAmount);

                usage.setUsedAt(now);

                // 18. Save ActivityCruiseUsage
                ActivityCruiseUsage savedUsage = activityCruiseUsageRepository.save(usage);

                return activityCruiseUsageMapper.toResponse(
                                savedUsage);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityCruiseUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId) {

                return activityCruiseUsageRepository
                                .findAllByBookingPassengerIdOrderByUsedAtDesc(
                                                bookingPassengerId)
                                .stream()
                                .map(activityCruiseUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityCruiseUsageResponse> getByUserId(
                        Long userId) {

                return activityCruiseUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                                                userId)
                                .stream()
                                .map(activityCruiseUsageMapper::toResponse)
                                .toList();
        }

        // Validate ActivityCruiseTour status
        private void validateActivityCruiseTourStatus(
                        String status) {

                if (status == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "ActivityCruiseTour chưa có trạng thái");
                }

                switch (status) {

                        case "IN_PROGRESS":
                                return;

                        case "WAITING_CONFIG":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "ActivityCruiseTour chưa được cấu hình");

                        case "CONFIGURED":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Activity Cruise chưa bắt đầu");

                        case "NOT_STARTED":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Activity Cruise chưa bắt đầu");

                        case "COMPLETED":
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Activity Cruise đã hoàn thành");

                        default:
                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Trạng thái ActivityCruiseTour không hợp lệ: "
                                                                + status);
                }
        }
}