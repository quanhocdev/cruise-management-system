package com.project.booking.service.shore;

import com.project.booking.client.VisitTourClient;
import com.project.booking.client.VisitTourClient.VisitTourUsageInfo;
import com.project.booking.client.VisitTourManagementClient;
import com.project.booking.dto.shore.ActivityVisitUsageResponse;
import com.project.booking.dto.shore.ActivityVisitUsageManagementInfo;
import com.project.booking.dto.shore.ActivityVisitUsageManagementResponse;
import com.project.booking.dto.shore.ActivityVisitUsageRequest;
import com.project.booking.exception.AppException;
import com.project.booking.kafka.ActivityVisitCompletedEventProducer;
import com.project.booking.mapper.ActivityVisitUsageMapper;
import com.project.booking.mapper.ActivityVisitUsageManagementMapper;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.service.common.benefit.BenefitConsumptionService;
import com.project.booking.service.common.pricing.PricingCalculator;
import com.project.booking.service.common.pricing.PricingResult;
import com.project.common.event.ActivityVisitCompletedEvent;

import org.springframework.http.HttpStatus;
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
public class ActivityVisitUsageServiceImpl implements ActivityVisitUsageService {

        private static final int DEFAULT_QUANTITY = 1;

        private final ActivityVisitUsageRepository activityVisitUsageRepository;
        private final ActivityVisitUsageMapper activityVisitUsageMapper;
        private final VisitTourClient visitTourClient;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final ActivityVisitUsageValidator validator;
        private final PricingCalculator pricingCalculator;
        private final BenefitConsumptionService benefitConsumptionService;
        private final VisitTourManagementClient visitTourManagementClient;
        private final ActivityVisitUsageManagementMapper activityVisitUsageManagementMapper;
        private final ActivityVisitCompletedEventProducer activityVisitCompletedEventProducer;

        public ActivityVisitUsageServiceImpl(
                        ActivityVisitUsageRepository activityVisitUsageRepository,
                        ActivityVisitUsageMapper activityVisitUsageMapper,
                        VisitTourClient visitTourClient,
                        BookingPassengerRepository bookingPassengerRepository,
                        ActivityVisitUsageValidator validator,
                        PricingCalculator pricingCalculator,
                        BenefitConsumptionService benefitConsumptionService,
                        VisitTourManagementClient visitTourManagementClient,
                        ActivityVisitUsageManagementMapper activityVisitUsageManagementMapper,
                        ActivityVisitCompletedEventProducer activityVisitCompletedEventProducer) {

                this.activityVisitUsageRepository = activityVisitUsageRepository;
                this.activityVisitUsageMapper = activityVisitUsageMapper;
                this.visitTourClient = visitTourClient;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.validator = validator;
                this.pricingCalculator = pricingCalculator;
                this.benefitConsumptionService = benefitConsumptionService;
                this.visitTourManagementClient = visitTourManagementClient;
                this.activityVisitUsageManagementMapper = activityVisitUsageManagementMapper;
                this.activityVisitCompletedEventProducer = activityVisitCompletedEventProducer;
        }

        @Override
        @Transactional
        public ActivityVisitUsageResponse create(ActivityVisitUsageRequest request) {

                LocalDateTime now = LocalDateTime.now();

                // 1. Hành khách + booking
                BookingPassenger bookingPassenger = bookingPassengerRepository
                                .findByNfcCardUid(request.nfcCardUid())
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy hành khách với NFC card UID: "
                                                                + request.nfcCardUid(),
                                                HttpStatus.NOT_FOUND));

                Booking booking = validator.validateBooking(bookingPassenger);

                // 2. Thông tin VisitTour
                VisitTourUsageInfo visitInfo = visitTourClient.getVisitTourUsageInfo(
                                request.visitTourId(),
                                booking.getTourPackageId());

                // 3. Kiểm tra booking và VisitTour
                // Áp dụng cho cả check-in và check-out
                validator.validateVisitTour(booking, visitInfo);

                // 4. Tìm lượt sử dụng chưa checkout
                List<ActivityVisitUsage> activeUsages = activityVisitUsageRepository
                                .findActiveUsagesForUpdate(
                                                bookingPassenger.getId(),
                                                request.visitTourId());

                if (!activeUsages.isEmpty()) {

                        // ================= CHECK-OUT =================

                        // Lấy lượt đang mở mới nhất
                        ActivityVisitUsage activeUsage = activeUsages.get(0);

                        activeUsage.setEndedAt(now);

                        ActivityVisitUsage savedUsage = activityVisitUsageRepository
                                        .save(activeUsage);

                        // Gửi Kafka sau khi transaction commit thành công
                        // Gửi Kafka sau khi transaction commit thành công
                        activityVisitCompletedEventProducer.sendAfterCommit(
                                        new ActivityVisitCompletedEvent(
                                                        booking.getId(),
                                                        bookingPassenger.getId(),
                                                        booking.getCreatedByUserId(),
                                                        request.visitTourId(),
                                                        savedUsage.getId(),
                                                        activityVisit.getName(),
                                                        savedUsage.getCreatedAt(),
                                                        savedUsage.getEndedAt()));

                        return activityVisitUsageMapper.toResponse(savedUsage);
                }

                // ================= CHECK-IN =================

                // 5. Kiểm tra trạng thái, thời gian, giá và discount
                validator.validateCheckIn(visitInfo, now);

                // 6. Kiểm tra sức chứa
                long currentPassengerCount = activityVisitUsageRepository
                                .countByVisitTourId(request.visitTourId());

                validator.validateCapacity(
                                visitInfo.maxPassengers(),
                                currentPassengerCount);

                // 7. Benefit + lock row
                int benefitQuantity = visitInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(visitInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                if (visitInfo.packageBenefitId() != null && benefitQuantity > 0) {

                        benefitConsumption = benefitConsumptionService.getForUpdate(
                                        booking.getId(),
                                        visitInfo.packageBenefitId());

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 8. Tính giá
                PricingResult amount = pricingCalculator.calculate(
                                visitInfo.price(),
                                visitInfo.discountPercent(),
                                DEFAULT_QUANTITY,
                                benefitQuantity,
                                usedBenefitQuantity);

                // 9. Cập nhật lượt benefit đã được áp dụng
                if (benefitConsumption != null && amount.discountedQuantity() > 0) {

                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        amount.discountedQuantity());
                }

                // 10. Tạo usage mới - CHECK-IN
                ActivityVisitUsage savedUsage = activityVisitUsageRepository.save(
                                buildUsage(bookingPassenger, request, amount, now));

                return activityVisitUsageMapper.toResponse(savedUsage);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityVisitUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId) {

                return activityVisitUsageRepository
                                .findAllByBookingPassengerIdOrderByUsedAtDesc(bookingPassengerId)
                                .stream()
                                .map(activityVisitUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityVisitUsageResponse> getByUserId(UUID userId) {

                return activityVisitUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                                                userId)
                                .stream()
                                .map(activityVisitUsageMapper::toResponse)
                                .toList();
        }

        private ActivityVisitUsage buildUsage(
                        BookingPassenger bookingPassenger,
                        ActivityVisitUsageRequest request,
                        PricingResult amount,
                        LocalDateTime now) {

                ActivityVisitUsage usage = new ActivityVisitUsage();

                usage.setBookingPassenger(bookingPassenger);
                usage.setVisitTourId(request.visitTourId());
                usage.setQuantity(DEFAULT_QUANTITY);
                usage.setUnitPrice(amount.unitPrice());
                usage.setDiscountAmount(amount.discountAmount());
                usage.setFinalAmount(amount.finalAmount());
                usage.setUsedAt(now);
                usage.setEndedAt(null);

                return usage;
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityVisitUsageManagementResponse> getManagementUsages() {

                List<ActivityVisitUsage> usages = activityVisitUsageRepository
                                .findAllByOrderByUsedAtDesc();

                if (usages.isEmpty()) {
                        return List.of();
                }

                List<UUID> visitTourIds = usages.stream()
                                .map(ActivityVisitUsage::getVisitTourId)
                                .distinct()
                                .toList();

                List<ActivityVisitUsageManagementInfo> visitInfos = visitTourManagementClient
                                .getVisitTourUsageInfo(visitTourIds);

                Map<UUID, ActivityVisitUsageManagementInfo> visitInfoMap = visitInfos.stream()
                                .collect(Collectors.toMap(
                                                ActivityVisitUsageManagementInfo::visitTourId,
                                                Function.identity(),
                                                (first, second) -> first));

                return usages.stream()
                                .map(usage -> activityVisitUsageManagementMapper.toResponse(
                                                usage,
                                                visitInfoMap.get(usage.getVisitTourId())))
                                .toList();
        }
}