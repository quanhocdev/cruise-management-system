package com.project.booking.service.shore;

import com.project.booking.client.VisitTourClient;
import com.project.booking.client.VisitTourClient.VisitTourUsageInfo;
import com.project.booking.dto.onboard.ActivityVisitUsageRequest;
import com.project.booking.dto.shore.ActivityVisitUsageResponse;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.ActivityVisitUsageMapper;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.service.BenefitConsumptionService;
import com.project.booking.service.shore.ActivityVisitUsageCalculator.UsageAmount;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.client.VisitTourManagementClient;
import com.project.booking.dto.shore.ActivityVisitUsageManagementInfo;
import com.project.booking.dto.shore.ActivityVisitUsageManagementResponse;
import com.project.booking.mapper.ActivityVisitUsageManagementMapper;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityVisitUsageServiceImpl implements ActivityVisitUsageService {

        private static final int DEFAULT_QUANTITY = 1;

        private final ActivityVisitUsageRepository activityVisitUsageRepository;
        private final ActivityVisitUsageMapper activityVisitUsageMapper;
        private final VisitTourClient visitTourClient;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final ActivityVisitUsageValidator validator;
        private final ActivityVisitUsageCalculator calculator;
        private final BenefitConsumptionService benefitConsumptionService;
        private final VisitTourManagementClient visitTourManagementClient;
        private final ActivityVisitUsageManagementMapper activityVisitUsageManagementMapper;

        public ActivityVisitUsageServiceImpl(
                        ActivityVisitUsageRepository activityVisitUsageRepository,
                        ActivityVisitUsageMapper activityVisitUsageMapper,
                        VisitTourClient visitTourClient,
                        BookingPassengerRepository bookingPassengerRepository,
                        ActivityVisitUsageValidator validator,
                        ActivityVisitUsageCalculator calculator,
                        BenefitConsumptionService benefitConsumptionService,
                        VisitTourManagementClient visitTourManagementClient,
                        ActivityVisitUsageManagementMapper activityVisitUsageManagementMapper) {

                this.activityVisitUsageRepository = activityVisitUsageRepository;
                this.activityVisitUsageMapper = activityVisitUsageMapper;
                this.visitTourClient = visitTourClient;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.validator = validator;
                this.calculator = calculator;
                this.benefitConsumptionService = benefitConsumptionService;
                this.visitTourManagementClient = visitTourManagementClient;
                this.activityVisitUsageManagementMapper = activityVisitUsageManagementMapper;
        }

        @Override
        @Transactional
        public ActivityVisitUsageResponse create(ActivityVisitUsageRequest request) {

                LocalDateTime now = LocalDateTime.now();

                // 1. Hành khách + booking
                BookingPassenger bookingPassenger = bookingPassengerRepository
                                .findByNfcCardUid(request.nfcCardUid())
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy hành khách với NFC card UID: " + request.nfcCardUid(),
                                                HttpStatus.NOT_FOUND));

                Booking booking = validator.validateBooking(bookingPassenger);

                // 2. Thông tin VisitTour
                VisitTourUsageInfo visitInfo = visitTourClient.getVisitTourUsageInfo(
                                request.visitTourId(),
                                booking.getTourPackageId());

                validator.validateVisitTour(booking, visitInfo, now);

                // 3. Sức chứa
                long currentPassengerCount = activityVisitUsageRepository
                                .countByVisitTourId(request.visitTourId());

                validator.validateCapacity(visitInfo.maxPassengers(), currentPassengerCount);

                // 4. Benefit (lock row nếu có benefit)
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

                // 5. Tính tiền
                UsageAmount amount = calculator.calculate(
                                visitInfo.price(),
                                visitInfo.discountPercent(),
                                DEFAULT_QUANTITY,
                                benefitQuantity,
                                usedBenefitQuantity);

                // 6. Trừ lượt benefit đã dùng
                if (benefitConsumption != null) {
                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        amount.freeQuantity());
                }

                // 7. Lưu usage
                ActivityVisitUsage savedUsage = activityVisitUsageRepository.save(
                                buildUsage(bookingPassenger, request, amount, now));

                return activityVisitUsageMapper.toResponse(savedUsage);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityVisitUsageResponse> getByBookingPassengerId(Long bookingPassengerId) {

                return activityVisitUsageRepository
                                .findAllByBookingPassengerIdOrderByUsedAtDesc(bookingPassengerId)
                                .stream()
                                .map(activityVisitUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ActivityVisitUsageResponse> getByUserId(Long userId) {

                return activityVisitUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(userId)
                                .stream()
                                .map(activityVisitUsageMapper::toResponse)
                                .toList();
        }

        private ActivityVisitUsage buildUsage(
                        BookingPassenger bookingPassenger,
                        ActivityVisitUsageRequest request,
                        UsageAmount amount,
                        LocalDateTime now) {

                ActivityVisitUsage usage = new ActivityVisitUsage();

                usage.setBookingPassenger(bookingPassenger);
                usage.setVisitTourId(request.visitTourId());
                usage.setQuantity(DEFAULT_QUANTITY);
                usage.setUnitPrice(amount.unitPrice());
                usage.setDiscountAmount(amount.discountAmount());
                usage.setFinalAmount(amount.finalAmount());
                usage.setUsedAt(now);

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