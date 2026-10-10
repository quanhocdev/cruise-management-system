package com.project.booking.service.service;

import com.project.booking.client.ServiceTourClient;
import com.project.booking.client.ServiceTourClient.ServiceUsageInfo;
import com.project.booking.dto.convenience.service.ServiceUsageRequest;
import com.project.booking.dto.convenience.service.ServiceUsageResponse;
import com.project.booking.mapper.ServiceUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ServiceUsage;
import com.project.booking.repository.ServiceUsageRepository;
import com.project.booking.service.BenefitConsumptionService;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.client.ServiceTourManagementClient;
import com.project.booking.client.ServiceTourManagementClient.ServiceUsageManagementInfo;
import com.project.booking.dto.convenience.service.ServiceUsageManagementResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceUsageServiceImpl implements ServiceUsageService {

    private final ServiceUsageRepository serviceUsageRepository;
    private final ServiceUsageMapper serviceUsageMapper;
    private final ServiceTourClient serviceTourClient;
    private final BenefitConsumptionService benefitConsumptionService;
    private final ServiceValidator serviceValidator;
    private final ServicePricingCalculator servicePricingCalculator;
    private final ServiceTourManagementClient serviceTourManagementClient;

    public ServiceUsageServiceImpl(
            ServiceUsageRepository serviceUsageRepository,
            ServiceUsageMapper serviceUsageMapper,
            ServiceTourClient serviceTourClient,
            BenefitConsumptionService benefitConsumptionService,
            ServiceValidator serviceValidator,
            ServicePricingCalculator servicePricingCalculator,
            ServiceTourManagementClient serviceTourManagementClient) {

        this.serviceUsageRepository = serviceUsageRepository;
        this.serviceUsageMapper = serviceUsageMapper;
        this.serviceTourClient = serviceTourClient;
        this.benefitConsumptionService = benefitConsumptionService;
        this.serviceValidator = serviceValidator;
        this.servicePricingCalculator = servicePricingCalculator;
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

        // 3 -> 6. Validate nghiệp vụ (tour, active, status, giá)
        serviceValidator.validateService(booking, serviceInfo);

        // 7. Passenger đang sử dụng Service này không?
        LocalDateTime now = LocalDateTime.now();

        ServiceUsage activeUsage = serviceUsageRepository.findActiveUsage(
                bookingPassenger.getId(),
                request.serviceTourId(),
                now).orElse(null);

        // 8. Đang sử dụng -> CHECKOUT
        if (activeUsage != null) {
            activeUsage.setEndedAt(now);
            ServiceUsage savedUsage = serviceUsageRepository.save(activeUsage);
            return serviceUsageMapper.toResponse(savedUsage);
        }

        // 9 -> 10. Chưa sử dụng -> CHECK-IN: kiểm tra chỗ + thời hạn
        serviceValidator.validateCapacity(
                request.serviceTourId(),
                serviceInfo.maxPassengers(),
                now);

        serviceValidator.validateDuration(serviceInfo.durationMinutes());

        LocalDateTime expiresAt = serviceInfo.durationMinutes() == null
                ? null
                : now.plusMinutes(serviceInfo.durationMinutes());

        // 11. Benefit + lock row
        UUID packageBenefitId = serviceInfo.packageBenefitId();
        int benefitQuantity = serviceInfo.benefitQuantity() == null
                ? 0
                : Math.max(serviceInfo.benefitQuantity(), 0);

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

        // 12 -> 16. Tính tiền
        var pricingResult = servicePricingCalculator.calculate(
                serviceInfo,
                usedBenefitQuantity);

        // 17. Cập nhật lượt benefit đã dùng
        if (benefitConsumption != null && pricingResult.freeUsage() > 0) {
            benefitConsumptionService.consume(
                    benefitConsumption,
                    pricingResult.freeUsage());
        }

        // 18 & 19. Tạo + lưu ServiceUsage (snapshot giá tại thời điểm sử dụng)
        ServiceUsage usage = new ServiceUsage();
        usage.setBookingPassenger(bookingPassenger);
        usage.setServiceTourId(request.serviceTourId());
        usage.setUnitPrice(pricingResult.unitPrice());
        usage.setDiscountAmount(pricingResult.discountAmount());
        usage.setFinalAmount(pricingResult.finalAmount());
        usage.setUsedAt(now);
        usage.setExpiresAt(expiresAt); // null nếu không có duration
        usage.setEndedAt(null); // vừa check-in nên chưa checkout

        ServiceUsage savedUsage = serviceUsageRepository.save(usage);

        return serviceUsageMapper.toResponse(savedUsage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceUsageResponse> getByBookingPassengerId(Long bookingPassengerId) {
        return serviceUsageRepository
                .findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId)
                .stream()
                .map(serviceUsageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceUsageResponse> getByUserId(Long userId) {
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

        List<ServiceUsageManagementInfo> serviceInfos = serviceTourManagementClient.getServiceUsageInfo(serviceTourIds);

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