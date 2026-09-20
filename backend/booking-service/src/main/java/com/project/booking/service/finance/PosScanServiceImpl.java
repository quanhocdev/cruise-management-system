package com.project.booking.service.finance;

import com.project.booking.dto.finance.QrScanRequest;
import com.project.booking.exception.AppException;
import com.project.booking.model.Booking;
import com.project.booking.repository.BookingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.project.booking.dto.finance.BookingScanResponse;

@Service
public class PosScanServiceImpl implements PosScanService {

    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PosScanServiceImpl(
            BookingRepository bookingRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.bookingRepository = bookingRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void processQrScan(QrScanRequest request) {

        // 1. Tìm booking trong DB dựa vào mã QR do máy POS gửi lên
        Booking booking = bookingRepository
                .findByBookingCodeIgnoreCase(request.bookingCode())
                .orElseThrow(() -> new AppException(
                        "Mã booking không tồn tại hoặc không hợp lệ: "
                                + request.bookingCode(),
                        HttpStatus.NOT_FOUND));

        // 2. Gói tin WebSocket siêu gọn
        BookingScanResponse response = new BookingScanResponse(
                booking.getId(),
                booking.getBookingCode());

        // 3. Bắn WebSocket xuống kênh riêng biệt của Tour
        String destination = "/topic/tour/"
                + booking.getTourId()
                + "/scans";

        messagingTemplate.convertAndSend(
                destination,
                response);
    }

}