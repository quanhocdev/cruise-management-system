package com.project.booking.service.onboard;

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
        private final ActivityCruiseValidator activityCruiseValidator;
        private final ActivityPricingCalculator activityPricingCalculator;

        public ActivityCruiseUsageServiceImpl(
                        ActivityCruiseUsageRepository activityCruiseUsageRepository,
                        ActivityCruiseUsageMapper activityCruiseUsageMapper,
                        ActivityCruiseClient activityCruiseClient,
                        BookingPassengerRepository bookingPassengerRepository,
                        BenefitConsumptionRepository benefitConsumptionRepository,
                        ActivityCruiseValidator activityCruiseValidator,
                        ActivityPricingCalculator activityPricingCalculator) {

                this.activityCruiseUsageRepository = activityCruiseUsageRepository;
                this.activityCruiseUsageMapper = activityCruiseUsageMapper;
                this.activityCruiseClient = activityCruiseClient;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.benefitConsumptionRepository = benefitConsumptionRepository;
                this.activityCruiseValidator = activityCruiseValidator;
                this.activityPricingCalculator = activityPricingCalculator;
        }

        @Override
        @Transactional
        public ActivityCruiseUsageResponse create(
                        ActivityCruiseUsageRequest request) {

                // 1. Validate hành khách bằng NFC qua Validator
                BookingPassenger bookingPassenger = activityCruiseValidator
                                .validateAndGetPassenger(request.nfcCardUid());

                Booking booking = bookingPassenger.getBooking();

                // 2. Gọi Tour Service lấy thông tin ActivityCruiseTour
                ActivityCruiseClient.ActivityCruiseUsageInfo activityInfo = activityCruiseClient
                                .getActivityCruiseUsageInfo(
                                                request.activityCruiseTourId(),
                                                booking.getTourPackageId());

                // 3 -> 7. Validate toàn bộ nghiệp vụ liên quan đến tour/thời gian/slot qua
                // Validator
                activityCruiseValidator.validateBookingAndActivity(
                                booking,
                                activityInfo,
                                request.activityCruiseTourId());

                // 8 & 9. Lấy thông tin PackageBenefit và khóa row (Lock row) chống Race
                // Condition
                UUID packageBenefitId = activityInfo.packageBenefitId();
                int benefitQuantity = activityInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(activityInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                if (packageBenefitId != null && benefitQuantity > 0) {
                        benefitConsumptionRepository.createIfNotExists(
                                        booking.getId(),
                                        packageBenefitId);

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

                // 10 -> 15. Tính toán giá tiền, discount qua Calculator
                var pricingResult = activityPricingCalculator.calculate(activityInfo, usedBenefitQuantity);

                // 16. Cập nhật BenefitConsumption nếu sử dụng suất miễn phí
                if (benefitConsumption != null && pricingResult.freeQuantityThisUsage() > 0) {
                        int currentUsedQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();

                        benefitConsumption.setUsedQuantity(
                                        currentUsedQuantity + (int) pricingResult.freeQuantityThisUsage());

                        benefitConsumptionRepository.save(benefitConsumption);
                }

                // 17 & 18. Tạo, lưu ActivityCruiseUsage và trả về response
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
}