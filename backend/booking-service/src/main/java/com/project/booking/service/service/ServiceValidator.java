package com.project.booking.service.service;

import com.project.booking.client.ServiceTourClient.ServiceUsageInfo;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.ServiceUsageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ServiceValidator {

    private final BookingPassengerRepository bookingPassengerRepository;
    private final ServiceUsageRepository serviceUsageRepository;

    public ServiceValidator(
            BookingPassengerRepository bookingPassengerRepository,
            ServiceUsageRepository serviceUsageRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.serviceUsageRepository = serviceUsageRepository;
    }

    public BookingPassenger validateAndGetPassenger(String nfcCardUid) {
        return bookingPassengerRepository.findByNfcCardUid(nfcCardUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hành khách với NFC card UID: " + nfcCardUid));
    }

    /** Validate booking TRƯỚC khi gọi Tour Service (vì cần tourPackageId). */
    public void validateBooking(Booking booking) {
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BookingPassenger không thuộc booking nào");
        }
        if (booking.getTourId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking không có tour");
        }
        if (booking.getTourPackageId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking không có tour package");
        }
    }

    /** Validate thông tin ServiceTour SAU khi gọi Tour Service. */
    public void validateService(Booking booking, ServiceUsageInfo serviceInfo) {
        if (serviceInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin ServiceTour");
        }
        if (!booking.getTourId().equals(serviceInfo.tourId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service không thuộc tour của booking");
        }
        if (!serviceInfo.serviceActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service hiện không còn hoạt động");
        }

        validateServiceTourStatus(serviceInfo.serviceTourStatus());

        if (serviceInfo.unitPrice() == null || serviceInfo.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá Service không hợp lệ");
        }
    }

    /** Kiểm tra còn chỗ khi CHECK-IN. */
    public void validateCapacity(UUID serviceTourId, Integer maxPassengers, LocalDateTime now) {
        if (maxPassengers == null || maxPassengers <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Maximum passengers của Service không hợp lệ");
        }

        long currentPassengers = serviceUsageRepository.countActiveUsages(serviceTourId, now);

        if (currentPassengers >= maxPassengers) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service hiện đã đủ số lượng hành khách");
        }
    }

    /** durationMinutes có thể null (không giới hạn), nhưng nếu có thì phải > 0. */
    public void validateDuration(Integer durationMinutes) {
        if (durationMinutes != null && durationMinutes <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian sử dụng Service không hợp lệ");
        }
    }

    private void validateServiceTourStatus(String status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ServiceTour chưa có trạng thái");
        }
        switch (status) {
            case "CONFIGURED":
            case "NOT_STARTED":
            case "IN_PROGRESS":
                return;
            case "WAITING_CONFIG":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ServiceTour chưa được cấu hình");
            case "COMPLETED":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ServiceTour đã hoàn thành");
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Trạng thái ServiceTour không hợp lệ: " + status);
        }
    }
}