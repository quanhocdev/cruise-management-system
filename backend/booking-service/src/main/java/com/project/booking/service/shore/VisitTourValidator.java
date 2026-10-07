package com.project.booking.service.shore;

import com.project.booking.client.VisitTourClient;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class VisitTourValidator {

    private final BookingPassengerRepository bookingPassengerRepository;
    private final ActivityVisitUsageRepository activityVisitUsageRepository;

    public VisitTourValidator(
            BookingPassengerRepository bookingPassengerRepository,
            ActivityVisitUsageRepository activityVisitUsageRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.activityVisitUsageRepository = activityVisitUsageRepository;
    }

    public BookingPassenger validateAndGetPassenger(String nfcCardUid) {
        return bookingPassengerRepository.findByNfcCardUid(nfcCardUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hành khách với NFC card UID: " + nfcCardUid));
    }

    public void validateBookingAndVisitTour(Booking booking, VisitTourClient.VisitTourUsageInfo visitInfo,
            UUID visitTourId) {
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

        validateVisitTourStatus(visitInfo.visitTourStatus());

        LocalDateTime now = LocalDateTime.now();

        if (visitInfo.startTime() == null || visitInfo.endTime() == null) {
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

        if (visitInfo.maxPassengers() == null || visitInfo.maxPassengers() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng hành khách tối đa của Visit Tour không hợp lệ");
        }

        long currentPassengerCount = activityVisitUsageRepository.countByVisitTourId(visitTourId);

        if (currentPassengerCount >= visitInfo.maxPassengers()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visit Tour đã đủ số lượng hành khách");
        }
    }

    private void validateVisitTourStatus(String status) {
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
                        "Trạng thái VisitTour không hợp lệ: " + status);
        }
    }
}