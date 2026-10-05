package com.project.booking.service;

import com.project.booking.client.VisitTourClient;
import com.project.booking.dto.ActivityVisitUsageRequest;
import com.project.booking.dto.ActivityVisitUsageResponse;
import com.project.booking.mapper.ActivityVisitUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.repository.ActivityVisitUsageRepository;
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
public class ActivityVisitUsageServiceImpl
        implements ActivityVisitUsageService {

    private final ActivityVisitUsageRepository activityVisitUsageRepository;
    private final ActivityVisitUsageMapper activityVisitUsageMapper;
    private final VisitTourClient visitTourClient;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BenefitConsumptionRepository benefitConsumptionRepository;

    public ActivityVisitUsageServiceImpl(
            ActivityVisitUsageRepository activityVisitUsageRepository,
            ActivityVisitUsageMapper activityVisitUsageMapper,
            VisitTourClient visitTourClient,
            BookingPassengerRepository bookingPassengerRepository,
            BenefitConsumptionRepository benefitConsumptionRepository) {

        this.activityVisitUsageRepository = activityVisitUsageRepository;
        this.activityVisitUsageMapper = activityVisitUsageMapper;
        this.visitTourClient = visitTourClient;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.benefitConsumptionRepository = benefitConsumptionRepository;
    }

    @Override
    @Transactional
    public ActivityVisitUsageResponse create(
            ActivityVisitUsageRequest request) {

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

        VisitTourClient.VisitTourUsageInfo visitInfo = visitTourClient.getVisitTourUsageInfo(
                request.visitTourId(),
                booking.getTourPackageId());

        if (visitInfo == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy thông tin VisitTour");
        }

        if (!booking.getTourId().equals(visitInfo.tourId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour không thuộc tour của booking");
        }

        validateVisitTourStatus(
                visitInfo.visitTourStatus());

        LocalDateTime now = LocalDateTime.now();

        if (visitInfo.startTime() == null
                || visitInfo.endTime() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour chưa được cấu hình thời gian");
        }

        if (now.isBefore(visitInfo.startTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour chưa bắt đầu");
        }

        if (now.isAfter(visitInfo.endTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour đã kết thúc");
        }

        if (visitInfo.maxPassengers() == null
                || visitInfo.maxPassengers() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng hành khách tối đa của Visit Tour không hợp lệ");
        }

        long currentPassengerCount = activityVisitUsageRepository
                .countByVisitTourId(request.visitTourId());

        if (currentPassengerCount >= visitInfo.maxPassengers()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour đã đủ số lượng hành khách");
        }

        int quantity = 1;

        if (visitInfo.price() == null
                || visitInfo.price().compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá Visit Tour không hợp lệ");
        }

        UUID packageBenefitId = visitInfo.packageBenefitId();

        int benefitQuantity = visitInfo.benefitQuantity() == null
                ? 0
                : Math.max(
                        visitInfo.benefitQuantity(),
                        0);

        BenefitConsumption benefitConsumption = null;
        long usedBenefitQuantity = 0;

        if (packageBenefitId != null
                && benefitQuantity > 0) {

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

        long remainingFreeQuantity = Math.max(
                (long) benefitQuantity
                        - usedBenefitQuantity,
                0);

        long freeQuantityThisUsage = Math.min(
                remainingFreeQuantity,
                quantity);

        long paidQuantityThisUsage = quantity - freeQuantityThisUsage;

        BigDecimal unitPrice = visitInfo.price()
                .setScale(
                        2,
                        RoundingMode.HALF_UP);

        BigDecimal grossAmount = unitPrice.multiply(
                BigDecimal.valueOf(quantity));

        BigDecimal freeDiscountAmount = unitPrice.multiply(
                BigDecimal.valueOf(
                        freeQuantityThisUsage));

        BigDecimal discountPercent = visitInfo.discountPercent() == null
                ? BigDecimal.ZERO
                : visitInfo.discountPercent();

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(
                        BigDecimal.valueOf(100)) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount percent không hợp lệ");
        }

        BigDecimal paidAmountBeforePercent = unitPrice.multiply(
                BigDecimal.valueOf(
                        paidQuantityThisUsage));

        BigDecimal percentDiscountAmount = paidAmountBeforePercent
                .multiply(discountPercent)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP);

        BigDecimal discountAmount = freeDiscountAmount
                .add(percentDiscountAmount)
                .setScale(
                        2,
                        RoundingMode.HALF_UP);

        BigDecimal finalAmount = grossAmount
                .subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(
                        2,
                        RoundingMode.HALF_UP);

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

        ActivityVisitUsage usage = new ActivityVisitUsage();

        usage.setBookingPassenger(
                bookingPassenger);

        usage.setVisitTourId(
                request.visitTourId());

        usage.setQuantity(quantity);
        usage.setUnitPrice(unitPrice);
        usage.setDiscountAmount(discountAmount);
        usage.setFinalAmount(finalAmount);
        usage.setUsedAt(now);

        ActivityVisitUsage savedUsage = activityVisitUsageRepository.save(
                usage);

        return activityVisitUsageMapper.toResponse(
                savedUsage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityVisitUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId) {

        return activityVisitUsageRepository
                .findAllByBookingPassengerIdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(activityVisitUsageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityVisitUsageResponse> getByUserId(Long userId) {

        return activityVisitUsageRepository
                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(
                        userId)
                .stream()
                .map(activityVisitUsageMapper::toResponse)
                .toList();
    }

    private void validateVisitTourStatus(
            String status) {

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "VisitTour chưa có trạng thái");
        }

        switch (status) {

            case "IN_PROGRESS":
                return;

            case "WAITING_CONFIG":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "VisitTour chưa được cấu hình");

            case "CONFIGURED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Visit Tour chưa bắt đầu");

            case "NOT_STARTED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Visit Tour chưa bắt đầu");

            case "DELAYED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Visit Tour đang bị trì hoãn");

            case "COMPLETED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Visit Tour đã hoàn thành");

            case "CANCELLED":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Visit Tour đã bị hủy");

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Trạng thái VisitTour không hợp lệ: "
                                + status);
        }
    }
}