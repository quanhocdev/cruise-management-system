package com.project.booking.service.onboard;

import com.project.booking.client.ActivityCruiseClient;
import com.project.booking.client.ActivityCruiseManagementClient;
import com.project.booking.dto.onboard.ActivityCruiseUsageManagementInfo;
import com.project.booking.dto.onboard.ActivityCruiseUsageManagementResponse;
import com.project.booking.dto.onboard.ActivityCruiseUsageRequest;
import com.project.booking.dto.onboard.ActivityCruiseUsageResponse;
import com.project.booking.mapper.ActivityCruiseUsageManagementMapper;
import com.project.booking.mapper.ActivityCruiseUsageMapper;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.service.BenefitConsumptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActivityCruiseUsageServiceImpl
                implements ActivityCruiseUsageService {

        private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
        private final ActivityCruiseUsageMapper activityCruiseUsageMapper;
        private final ActivityCruiseUsageManagementMapper activityCruiseUsageManagementMapper;
        private final ActivityCruiseClient activityCruiseClient;
        private final ActivityCruiseManagementClient activityCruiseManagementClient;
        private final BenefitConsumptionService benefitConsumptionService;
        private final ActivityCruiseValidator activityCruiseValidator;
        private final ActivityPricingCalculator activityPricingCalculator;

        public ActivityCruiseUsageServiceImpl(
                        ActivityCruiseUsageRepository activityCruiseUsageRepository,
                        ActivityCruiseUsageMapper activityCruiseUsageMapper,
                        ActivityCruiseUsageManagementMapper activityCruiseUsageManagementMapper,
                        ActivityCruiseClient activityCruiseClient,
                        ActivityCruiseManagementClient activityCruiseManagementClient,
                        BenefitConsumptionService benefitConsumptionService,
                        ActivityCruiseValidator activityCruiseValidator,
                        ActivityPricingCalculator activityPricingCalculator) {

                this.activityCruiseUsageRepository = activityCruiseUsageRepository;
                this.activityCruiseUsageMapper = activityCruiseUsageMapper;
                this.activityCruiseUsageManagementMapper = activityCruiseUsageManagementMapper;
                this.activityCruiseClient = activityCruiseClient;
                this.activityCruiseManagementClient = activityCruiseManagementClient;
                this.benefitConsumptionService = benefitConsumptionService;
                this.activityCruiseValidator = activityCruiseValidator;
                this.activityPricingCalculator = activityPricingCalculator;
        }

        @Override
        @Transactional
        public ActivityCruiseUsageResponse create(
                        ActivityCruiseUsageRequest request) {

                // 1. Validate hành khách bằng NFC
                BookingPassenger bookingPassenger = activityCruiseValidator
                                .validateAndGetPassenger(request.nfcCardUid());

                Booking booking = bookingPassenger.getBooking();

                // 2. Lấy thông tin ActivityCruiseTour
                ActivityCruiseClient.ActivityCruiseUsageInfo activityInfo = activityCruiseClient
                                .getActivityCruiseUsageInfo(
                                                request.activityCruiseTourId(),
                                                booking.getTourPackageId());

                // 3 -> 7. Validate nghiệp vụ
                activityCruiseValidator.validateBookingAndActivity(
                                booking,
                                activityInfo,
                                request.activityCruiseTourId());

                // 8 & 9. Benefit + lock row
                UUID packageBenefitId = activityInfo.packageBenefitId();
                int benefitQuantity = activityInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(activityInfo.benefitQuantity(), 0);

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

                // 10 -> 15. Tính tiền
                var pricingResult = activityPricingCalculator.calculate(
                                activityInfo,
                                usedBenefitQuantity);

                // 16. Cập nhật lượt benefit đã dùng
                if (benefitConsumption != null) {
                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        pricingResult.freeQuantityThisUsage());
                }

                // 17 & 18. Lưu usage
                ActivityCruiseUsage usage = new ActivityCruiseUsage();
                usage.setBookingPassenger(bookingPassenger);
                usage.setActivityCruiseTourId(request.activityCruiseTourId());
                usage.setQuantity(1);
                usage.setUnitPrice(pricingResult.unitPrice());
                usage.setDiscountAmount(pricingResult.discountAmount());
                usage.setFinalAmount(pricingResult.finalAmount());
                usage.setUsedAt(LocalDateTime.now());

                ActivityCruiseUsage savedUsage = activityCruiseUsageRepository.save(usage);

                return activityCruiseUsageMapper.toResponse(savedUsage);
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

        @Override
        @Transactional(readOnly = true)
        public List<ActivityCruiseUsageManagementResponse> getManagementUsages() {

                List<ActivityCruiseUsage> usages = activityCruiseUsageRepository
                                .findAllByOrderByUsedAtDesc();

                if (usages.isEmpty()) {
                        return List.of();
                }

                List<UUID> activityCruiseTourIds = usages.stream()
                                .map(ActivityCruiseUsage::getActivityCruiseTourId)
                                .distinct()
                                .toList();

                List<ActivityCruiseUsageManagementInfo> activityInfos = activityCruiseManagementClient
                                .getActivityCruiseUsageInfo(
                                                activityCruiseTourIds);

                Map<UUID, ActivityCruiseUsageManagementInfo> activityInfoMap = activityInfos.stream()
                                .collect(Collectors.toMap(
                                                ActivityCruiseUsageManagementInfo::activityCruiseTourId,
                                                Function.identity()));

                return usages.stream()
                                .map(usage -> activityCruiseUsageManagementMapper.toResponse(
                                                usage,
                                                activityInfoMap.get(
                                                                usage.getActivityCruiseTourId())))
                                .toList();
        }
}