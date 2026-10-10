package com.project.booking.service.shore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.project.booking.client.VisitTourClient.VisitTourUsageInfo;
import com.project.booking.exception.AppException;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;

@Component
public class ActivityVisitUsageValidator {

    public Booking validateBooking(BookingPassenger bookingPassenger) {
        Booking booking = bookingPassenger.getBooking();

        if (booking == null) {
            throw badRequest("BookingPassenger không thuộc booking nào");
        }
        if (booking.getTourId() == null) {
            throw badRequest("Booking không có tour");
        }
        if (booking.getTourPackageId() == null) {
            throw badRequest("Booking không có tour package");
        }
        return booking;
    }

    public void validateVisitTour(Booking booking, VisitTourUsageInfo visitInfo, LocalDateTime now) {
        if (visitInfo == null) {
            throw new AppException("Không tìm thấy thông tin VisitTour", HttpStatus.NOT_FOUND);
        }
        if (!booking.getTourId().equals(visitInfo.tourId())) {
            throw badRequest("Visit Tour không thuộc tour của booking");
        }

        validateStatus(visitInfo.visitTourStatus());
        validateTime(visitInfo, now);
        validatePrice(visitInfo.price());
        validateDiscountPercent(visitInfo.discountPercent());
    }

    public void validateCapacity(Integer maxPassengers, long currentPassengerCount) {
        if (maxPassengers == null || maxPassengers <= 0) {
            throw badRequest("Số lượng hành khách tối đa của Visit Tour không hợp lệ");
        }
        if (currentPassengerCount >= maxPassengers) {
            throw badRequest("Visit Tour đã đủ số lượng hành khách");
        }
    }

    private void validateTime(VisitTourUsageInfo visitInfo, LocalDateTime now) {
        if (visitInfo.startTime() == null || visitInfo.endTime() == null) {
            throw badRequest("Visit Tour chưa được cấu hình thời gian");
        }
        if (now.isBefore(visitInfo.startTime())) {
            throw badRequest("Visit Tour chưa bắt đầu");
        }
        if (now.isAfter(visitInfo.endTime())) {
            throw badRequest("Visit Tour đã kết thúc");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw badRequest("Giá Visit Tour không hợp lệ");
        }
    }

    private void validateDiscountPercent(BigDecimal discountPercent) {
        if (discountPercent == null) {
            return;
        }
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw badRequest("Discount percent không hợp lệ");
        }
    }

    private void validateStatus(String status) {
        if (status == null) {
            throw badRequest("VisitTour chưa có trạng thái");
        }

        switch (status) {
            case "IN_PROGRESS" -> {
            }
            case "WAITING_CONFIG" -> throw badRequest("VisitTour chưa được cấu hình");
            case "CONFIGURED", "NOT_STARTED" -> throw badRequest("Visit Tour chưa bắt đầu");
            case "DELAYED" -> throw badRequest("Visit Tour đang bị trì hoãn");
            case "COMPLETED" -> throw badRequest("Visit Tour đã hoàn thành");
            case "CANCELLED" -> throw badRequest("Visit Tour đã bị hủy");
            default -> throw badRequest("Trạng thái VisitTour không hợp lệ: " + status);
        }
    }

    private AppException badRequest(String message) {
        return new AppException(message, HttpStatus.BAD_REQUEST);
    }
}