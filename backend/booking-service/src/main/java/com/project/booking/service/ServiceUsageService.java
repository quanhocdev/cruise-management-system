package com.project.booking.service;

import com.project.booking.client.ServiceTourClient;
import com.project.booking.client.ServiceTourClient.ServiceUsageInfo;
import com.project.booking.dto.ServiceUsageRequest;
import com.project.booking.dto.ServiceUsageResponse;
import com.project.booking.mapper.ServiceUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ServiceUsage;
import com.project.booking.repository.BenefitConsumptionRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.ServiceUsageRepository;
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
public class ServiceUsageService {

    private final ServiceUsageRepository serviceUsageRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final ServiceUsageMapper serviceUsageMapper;
    private final ServiceTourClient serviceTourClient;
    private final BenefitConsumptionRepository benefitConsumptionRepository;

    public ServiceUsageService(
            ServiceUsageRepository serviceUsageRepository,
            BookingPassengerRepository bookingPassengerRepository,
            ServiceUsageMapper serviceUsageMapper,
            ServiceTourClient serviceTourClient,
            BenefitConsumptionRepository benefitConsumptionRepository) {

        this.serviceUsageRepository = serviceUsageRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.serviceUsageMapper = serviceUsageMapper;
        this.serviceTourClient = serviceTourClient;
        this.benefitConsumptionRepository = benefitConsumptionRepository;
    }

    @Transactional
    public ServiceUsageResponse scan(ServiceUsageRequest request) {

        // =========================================================
        // 1. Tìm BookingPassenger bằng NFC
        // =========================================================

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

        // =========================================================
        // 2. Gọi Tour Service lấy thông tin ServiceTour
        // =========================================================

        ServiceUsageInfo serviceInfo = serviceTourClient.getServiceUsageInfo(
                request.serviceTourId(),
                booking.getTourPackageId());

        if (serviceInfo == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy thông tin ServiceTour");
        }

        // =========================================================
        // 3. Kiểm tra ServiceTour thuộc đúng Tour
        // =========================================================

        if (!booking.getTourId().equals(serviceInfo.tourId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service không thuộc tour của booking");
        }

        // =========================================================
        // 4. Kiểm tra Service còn ACTIVE
        // =========================================================

        if (!serviceInfo.serviceActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Service hiện không còn hoạt động");
        }

        // =========================================================
        // 5. Kiểm tra ServiceTour status
        // =========================================================

        validateServiceTourStatus(
                serviceInfo.serviceTourStatus());

        // =========================================================
        // 6. Kiểm tra giá
        // =========================================================

        if (serviceInfo.unitPrice() == null
                || serviceInfo.unitPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá Service không hợp lệ");
        }

        // =========================================================
        // 7. Kiểm tra passenger hiện có đang sử dụng Service này không
        // =========================================================

        LocalDateTime now = LocalDateTime.now();

        ServiceUsage activeUsage = serviceUsageRepository.findActiveUsage(
                bookingPassenger.getId(),
                request.serviceTourId(),
                now).orElse(null);

        // =========================================================
        // 8. Nếu đang sử dụng → CHECKOUT
        // =========================================================

        if (activeUsage != null) {

            activeUsage.setEndedAt(now);

            ServiceUsage savedUsage = serviceUsageRepository.save(activeUsage);

            return serviceUsageMapper.toResponse(savedUsage);
        }

        // =========================================================
        // 9. Không có active usage → CHECK-IN
        //
        // Có thể xảy ra:
        // - Passenger chưa từng dùng
        // - Passenger đã checkout trước đó
        // - Usage trước đó đã hết hạn
        // =========================================================

        validateCapacity(
                request.serviceTourId(),
                serviceInfo.maxPassengers(),
                now);

        // =========================================================
        // 10. Xác định thời gian hết hạn
        // =========================================================

        LocalDateTime expiresAt = null;

        if (serviceInfo.durationMinutes() != null) {

            if (serviceInfo.durationMinutes() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Thời gian sử dụng Service không hợp lệ");
            }

            expiresAt = now.plusMinutes(
                    serviceInfo.durationMinutes());
        }

        // =========================================================
        // 11. Lấy thông tin PackageBenefit
        // =========================================================

        UUID packageBenefitId = serviceInfo.packageBenefitId();

        int benefitQuantity = serviceInfo.benefitQuantity() == null
                ? 0
                : Math.max(serviceInfo.benefitQuantity(), 0);

        BenefitConsumption benefitConsumption = null;

        long usedBenefitQuantity = 0;

        if (packageBenefitId != null && benefitQuantity > 0) {

            // Đảm bảo BenefitConsumption tồn tại
            benefitConsumptionRepository.createIfNotExists(
                    booking.getId(),
                    packageBenefitId);

            // Lock row để chống race condition
            benefitConsumption = benefitConsumptionRepository.findForUpdate(
                    booking.getId(),
                    packageBenefitId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Không tìm thấy BenefitConsumption"));

            usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                    ? 0
                    : benefitConsumption.getUsedQuantity();
        }

        // =========================================================
        // 12. ServiceUsage = 1 passenger
        // =========================================================

        long remainingFreeQuantity = Math.max(
                (long) benefitQuantity - usedBenefitQuantity,
                0);

        // Mỗi lần ServiceUsage tương ứng 1 passenger
        long freeUsage = Math.min(
                remainingFreeQuantity,
                1);

        long paidUsage = 1 - freeUsage;

        // =========================================================
        // 13. Tính tiền
        // =========================================================

        BigDecimal unitPrice = serviceInfo.unitPrice()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal grossAmount = unitPrice;

        // Miễn phí theo benefit
        BigDecimal freeDiscountAmount = unitPrice.multiply(
                BigDecimal.valueOf(freeUsage));

        // =========================================================
        // 14. Discount percent
        // =========================================================

        BigDecimal discountPercent = serviceInfo.discountPercent() == null
                ? BigDecimal.ZERO
                : serviceInfo.discountPercent();

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount percent không hợp lệ");
        }

        BigDecimal paidAmountBeforePercent = unitPrice.multiply(
                BigDecimal.valueOf(paidUsage));

        BigDecimal percentDiscountAmount = paidAmountBeforePercent
                .multiply(discountPercent)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP);

        // =========================================================
        // 15. Tổng discount
        // =========================================================

        BigDecimal discountAmount = freeDiscountAmount
                .add(percentDiscountAmount)
                .setScale(
                        2,
                        RoundingMode.HALF_UP);

        // =========================================================
        // 16. Final amount
        // =========================================================

        BigDecimal finalAmount = grossAmount
                .subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(
                        2,
                        RoundingMode.HALF_UP);

        // =========================================================
        // 17. Cập nhật BenefitConsumption
        // =========================================================

        if (benefitConsumption != null
                && freeUsage > 0) {

            int currentUsedQuantity = benefitConsumption.getUsedQuantity() == null
                    ? 0
                    : benefitConsumption.getUsedQuantity();

            benefitConsumption.setUsedQuantity(
                    currentUsedQuantity + (int) freeUsage);

            benefitConsumptionRepository.save(
                    benefitConsumption);
        }

        // =========================================================
        // 18. Tạo ServiceUsage
        // =========================================================

        ServiceUsage usage = new ServiceUsage();

        usage.setBookingPassenger(bookingPassenger);
        usage.setServiceTourId(request.serviceTourId());

        // Snapshot giá tại thời điểm sử dụng
        usage.setUnitPrice(unitPrice);

        usage.setDiscountAmount(discountAmount);
        usage.setFinalAmount(finalAmount);

        // Bắt đầu sử dụng
        usage.setUsedAt(now);

        // Có duration → tự hết hạn
        // Không có duration → null
        usage.setExpiresAt(expiresAt);

        // Vừa check-in nên chưa checkout
        usage.setEndedAt(null);

        // =========================================================
        // 19. Save ServiceUsage
        // =========================================================

        ServiceUsage savedUsage = serviceUsageRepository.save(usage);

        return serviceUsageMapper.toResponse(savedUsage);
    }

    // =============================================================
    // Lấy lịch sử sử dụng của passenger
    // =============================================================

    @Transactional(readOnly = true)
    public List<ServiceUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId) {

        return serviceUsageRepository
                .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(serviceUsageMapper::toResponse)
                .toList();
    }
    // =============================================================
    // Lấy toàn bộ lịch sử sử dụng service
    // của các passenger thuộc booking do user tạo
    // =============================================================

    @Transactional(readOnly = true)
    public List<ServiceUsageResponse> getByUserId(Long userId) {

        return serviceUsageRepository
                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                        userId)
                .stream()
                .map(serviceUsageMapper::toResponse)
                .toList();
    }

    // =============================================================
    // Kiểm tra capacity
    // =============================================================

    private void validateCapacity(
            UUID serviceTourId,
            Integer maxPassengers,
            LocalDateTime now) {

        if (maxPassengers == null || maxPassengers <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum passengers của Service không hợp lệ");
        }

        long currentPassengers = serviceUsageRepository.countActiveUsages(
                serviceTourId,
                now);

        if (currentPassengers >= maxPassengers) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Service hiện đã đủ số lượng hành khách");
        }
    }

    // =============================================================
    // Validate ServiceTour status
    // =============================================================

    private void validateServiceTourStatus(String status) {

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ServiceTour chưa có trạng thái");
        }

        switch (status) {

            case "CONFIGURED":
            case "NOT_STARTED":
            case "IN_PROGRESS":
                return;

            case "WAITING_CONFIG":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "ServiceTour chưa được cấu hình");

            case "COMPLETED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "ServiceTour đã hoàn thành");

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Trạng thái ServiceTour không hợp lệ: "
                                + status);
        }
    }
}