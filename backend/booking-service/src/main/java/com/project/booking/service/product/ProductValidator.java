package com.project.booking.service.product;

import com.project.booking.client.TourProductClient;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Component
public class ProductValidator {

    private final BookingPassengerRepository bookingPassengerRepository;

    public ProductValidator(BookingPassengerRepository bookingPassengerRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
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

    /** Validate thông tin ProductTour SAU khi gọi Tour Service. */
    public void validateProduct(Booking booking, TourProductClient.ProductUsageInfo productInfo) {
        if (productInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin ProductTour");
        }
        if (!booking.getTourId().equals(productInfo.tourId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product không thuộc tour của booking");
        }
        if (!productInfo.productActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product hiện không còn hoạt động");
        }

        validateProductTourStatus(productInfo.productTourStatus());

        if (productInfo.unitPrice() == null || productInfo.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá Product không hợp lệ");
        }
    }

    private void validateProductTourStatus(String status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductTour chưa có trạng thái");
        }
        switch (status) {
            case "CONFIGURED":
            case "NOT_STARTED":
            case "IN_PROGRESS":
                return;
            case "WAITING_CONFIG":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductTour chưa được cấu hình");
            case "OUT_OF_STOCK":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product đã hết hàng");
            case "COMPLETED":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductTour đã hoàn thành");
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Trạng thái ProductTour không hợp lệ: " + status);
        }
    }
}