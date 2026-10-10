package com.project.booking.service.onboard;

import com.project.booking.client.ActivityCruiseClient;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ActivityCruiseValidator {

    private final BookingPassengerRepository bookingPassengerRepository;
    private final ActivityCruiseUsageRepository activityCruiseUsageRepository;

    public ActivityCruiseValidator(
            BookingPassengerRepository bookingPassengerRepository,
            ActivityCruiseUsageRepository activityCruiseUsageRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.activityCruiseUsageRepository = activityCruiseUsageRepository;
    }

    public BookingPassenger validateAndGetPassenger(String nfcCardUid) {
        return bookingPassengerRepository.findByNfcCardUid(nfcCardUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hành khách với NFC card UID: " + nfcCardUid));
    }

    public void validateBookingAndActivity(Booking booking, ActivityCruiseClient.ActivityCruiseUsageInfo activityInfo,
            UUID activityCruiseTourId) {
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BookingPassenger không thuộc booking nào");
        }
        if (booking.getTourId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking không có tour");
        }
        if (booking.getTourPackageId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking không có tour package");
        }
        if (activityInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin ActivityCruiseTour");
        }
        if (!booking.getTourId().equals(activityInfo.tourId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise không thuộc tour của booking");
        }
        if (!activityInfo.activityActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise hiện không còn hoạt động");
        }

        validateActivityCruiseTourStatus(activityInfo.activityCruiseTourStatus());

        LocalDateTime now = LocalDateTime.now();
        if (activityInfo.startTime() == null || activityInfo.endTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise chưa được cấu hình thời gian");
        }
        if (now.isBefore(activityInfo.startTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise chưa bắt đầu");
        }
        if (now.isAfter(activityInfo.endTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise đã kết thúc");
        }

        if (activityInfo.maxPassengers() == null || activityInfo.maxPassengers() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Số lượng hành khách tối đa của Activity Cruise không hợp lệ");
        }

        long currentPassengerCount = activityCruiseUsageRepository.countByActivityCruiseTourId(activityCruiseTourId);

        if (currentPassengerCount >= activityInfo.maxPassengers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise đã đủ số lượng hành khách");
        }
    }

    private void validateActivityCruiseTourStatus(String status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ActivityCruiseTour chưa có trạng thái");
        }
        switch (status) {
            case "IN_PROGRESS":
                return;
            case "WAITING_CONFIG":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ActivityCruiseTour chưa được cấu hình");
            case "CONFIGURED":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ActivityCruiseTour chưa bắt đầu");
            case "NOT_STARTED":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise chưa bắt đầu");
            case "COMPLETED":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity Cruise đã hoàn thành");
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Trạng thái ActivityCruiseTour không hợp lệ: " + status);
        }
    }
}