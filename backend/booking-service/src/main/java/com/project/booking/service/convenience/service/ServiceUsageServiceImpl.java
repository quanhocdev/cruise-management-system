
package com.project.booking.service.convenience.service;

import com.project.booking.client.ServiceTourClient;
import com.project.booking.client.ServiceTourClient.ServiceUsageInfo;
import com.project.booking.client.ServiceTourManagementClient;
import com.project.booking.client.ServiceTourManagementClient.ServiceUsageManagementInfo;
import com.project.booking.dto.convenience.service.ServiceUsageManagementResponse;
import com.project.booking.dto.convenience.service.ServiceUsageRequest;
import com.project.booking.dto.convenience.service.ServiceUsageResponse;
import com.project.booking.mapper.ServiceUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ServiceUsage;
import com.project.booking.repository.ServiceUsageRepository;
import com.project.booking.service.common.benefit.BenefitConsumptionService;
import com.project.booking.service.common.pricing.PricingCalculator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.project.booking.kafka.ServiceCompletedEventProducer;
import com.project.common.event.ServiceCompletedEvent;
import java.time.Instant;

@Service
public class ServiceUsageServiceImpl implements ServiceUsageService {

        private static final long USAGE_QUANTITY = 1L;
        private final ServiceCompletedEventProducer serviceCompletedEventProducer;
        private final ServiceUsageRepository serviceUsageRepository;
        private final ServiceUsageMapper serviceUsageMapper;
        private final ServiceTourClient serviceTourClient;
        private final BenefitConsumptionService benefitConsumptionService;
        private final ServiceValidator serviceValidator;
        private final PricingCalculator pricingCalculator;
        private final ServiceTourManagementClient serviceTourManagementClient;

        public ServiceUsageServiceImpl(
                        ServiceCompletedEventProducer serviceCompletedEventProducer,
                        ServiceUsageRepository serviceUsageRepository,
                        ServiceUsageMapper serviceUsageMapper,
                        ServiceTourClient serviceTourClient,
                        BenefitConsumptionService benefitConsumptionService,
                        ServiceValidator serviceValidator,
                        PricingCalculator pricingCalculator,
                        ServiceTourManagementClient serviceTourManagementClient) {

                this.serviceCompletedEventProducer = serviceCompletedEventProducer;
                this.serviceUsageRepository = serviceUsageRepository;
                this.serviceUsageMapper = serviceUsageMapper;
                this.serviceTourClient = serviceTourClient;
                this.benefitConsumptionService = benefitConsumptionService;
                this.serviceValidator = serviceValidator;
                this.pricingCalculator = pricingCalculator;
                this.serviceTourManagementClient = serviceTourManagementClient;
        }

        @Override
        @Transactional
        public ServiceUsageResponse scan(ServiceUsageRequest request) {

                // 1. Validate hành khách bằng NFC + booking
                BookingPassenger bookingPassenger = serviceValidator
                                .validateAndGetPassenger(request.nfcCardUid());

                Booking booking = bookingPassenger.getBooking();
                serviceValidator.validateBooking(booking);

                // 2. Lấy thông tin ServiceTour từ Tour Service
                ServiceUsageInfo serviceInfo = serviceTourClient.getServiceUsageInfo(
                                request.serviceTourId(),
                                booking.getTourPackageId());

                // 3. Validate nghiệp vụ service
                serviceValidator.validateService(booking, serviceInfo);

                LocalDateTime now = LocalDateTime.now();

                // 4. Kiểm tra hành khách có đang sử dụng service này không
                ServiceUsage activeUsage = serviceUsageRepository.findActiveUsage(
                                bookingPassenger.getId(),
                                request.serviceTourId(),
                                now).orElse(null);

                // 5. Đang sử dụng -> CHECK-OUT
                // 5. Đang sử dụng -> CHECK-OUT
                if (activeUsage != null) {

                        activeUsage.setEndedAt(now);

                        ServiceUsage savedUsage = serviceUsageRepository.save(activeUsage);

                        // Phát event sau khi transaction commit thành công
                        serviceCompletedEventProducer.sendAfterCommit(
                                        new ServiceCompletedEvent(
                                                        booking.getId(),
                                                        bookingPassenger.getId(),
                                                        booking.getCreatedByUserId(),
                                                        request.serviceTourId(),
                                                        savedUsage.getId(),
                                                        service.getName(),
                                                        savedUsage.getUsedAt(),
                                                        savedUsage.getEndedAt()));

                        return serviceUsageMapper.toResponse(savedUsage);
                }
                // 6. Chưa sử dụng -> CHECK-IN: kiểm tra sức chứa và thời hạn
                serviceValidator.validateCapacity(
                                request.serviceTourId(),
                                serviceInfo.maxPassengers(),
                                now);

                serviceValidator.validateDuration(serviceInfo.durationMinutes());

                LocalDateTime expiresAt = serviceInfo.durationMinutes() == null
                                ? null
                                : now.plusMinutes(serviceInfo.durationMinutes());

                // 7. Lấy thông tin benefit
                UUID packageBenefitId = serviceInfo.packageBenefitId();

                int benefitQuantity = serviceInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(serviceInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                // 8. Lock BenefitConsumption để tránh sử dụng benefit đồng thời
                if (packageBenefitId != null && benefitQuantity > 0) {

                        benefitConsumption = benefitConsumptionService.getForUpdate(
                                        booking.getId(),
                                        packageBenefitId);

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 9. Tính giá bằng PricingCalculator dùng chung
                var pricingResult = pricingCalculator.calculate(
                                serviceInfo.unitPrice(),
                                serviceInfo.discountPercent(),
                                USAGE_QUANTITY,
                                benefitQuantity,
                                usedBenefitQuantity);

                // 10. Cập nhật số lượng benefit đã được áp dụng
                if (benefitConsumption != null
                                && pricingResult.discountedQuantity() > 0) {

                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        pricingResult.discountedQuantity());
                }

                // 11. Tạo ServiceUsage với snapshot giá tại thời điểm check-in
                ServiceUsage usage = new ServiceUsage();
                usage.setBookingPassenger(bookingPassenger);
                usage.setServiceTourId(request.serviceTourId());
                usage.setUnitPrice(pricingResult.unitPrice());
                usage.setDiscountAmount(pricingResult.discountAmount());
                usage.setFinalAmount(pricingResult.finalAmount());
                usage.setUsedAt(now);
                usage.setExpiresAt(expiresAt);
                usage.setEndedAt(null);

                ServiceUsage savedUsage = serviceUsageRepository.save(usage);

                return serviceUsageMapper.toResponse(savedUsage);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId) {

                return serviceUsageRepository
                                .findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId)
                                .stream()
                                .map(serviceUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceUsageResponse> getByUserId(UUID userId) {

                return serviceUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(userId)
                                .stream()
                                .map(serviceUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceUsageManagementResponse> getManagementUsages() {

                List<ServiceUsage> usages = serviceUsageRepository
                                .findAllByOrderByUsedAtDesc();

                if (usages.isEmpty()) {
                        return List.of();
                }

                List<UUID> serviceTourIds = usages.stream()
                                .map(ServiceUsage::getServiceTourId)
                                .filter(java.util.Objects::nonNull)
                                .distinct()
                                .toList();

                List<ServiceUsageManagementInfo> serviceInfos = serviceTourManagementClient
                                .getServiceUsageInfo(serviceTourIds);

                Map<UUID, ServiceUsageManagementInfo> serviceInfoMap = serviceInfos.stream()
                                .collect(Collectors.toMap(
                                                ServiceUsageManagementInfo::serviceTourId,
                                                Function.identity(),
                                                (first, second) -> first));

                return usages.stream()
                                .map(usage -> serviceUsageMapper.toManagementResponse(
                                                usage,
                                                serviceInfoMap.get(usage.getServiceTourId())))
                                .toList();
        }
}
