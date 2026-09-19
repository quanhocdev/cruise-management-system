package com.project.booking.service.finance;

import com.project.booking.dto.finance.PassengerCheckInRequest;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingPassengerStatus;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CheckPassengerServiceImpl implements CheckPassengerService {

    private final BookingPassengerRepository bookingPassengerRepository;

    public CheckPassengerServiceImpl(BookingPassengerRepository bookingPassengerRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
    }

    @Override
    @Transactional
    public void processSinglePassengerCheckIn(Long bookingId, PassengerCheckInRequest request) {
        System.out.println("========================================");
        System.out.println("[SERVICE] Đang xử lý check-in cho hành khách ID: " + request.getPassengerId());
        System.out.println("[SERVICE] Booking ID: " + bookingId);
        System.out.println("[SERVICE] Gán Room ID: " + request.getRoomId());
        System.out.println("[SERVICE] Gán NFC UID: " + request.getNfcCode());
        System.out.println("========================================");

        // 1. Tìm bản ghi hành khách trong đơn đặt tour (Dùng đúng tên hàm của
        // Repository)
        BookingPassenger passengerAssignment = bookingPassengerRepository
                .findByBooking_IdAndPassenger_Id(bookingId, request.getPassengerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hành khách trong đơn đặt tour này!"));

        // 2. Gán thông tin phòng, mã thẻ NFC và cập nhật trạng thái
        passengerAssignment.setRoomId(request.getRoomId());
        passengerAssignment.setNfcCardUid(request.getNfcCode());
        passengerAssignment.setStatus(BookingPassengerStatus.CHECKED_IN);
        passengerAssignment.setCheckedInAt(LocalDateTime.now());

        // 3. Lưu lại vào database
        bookingPassengerRepository.save(passengerAssignment);

        System.out.println("[SERVICE] Check-in thành công cho hành khách!");
    }
}