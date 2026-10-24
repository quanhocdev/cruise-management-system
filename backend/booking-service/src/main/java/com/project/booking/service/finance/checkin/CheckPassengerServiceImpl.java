
package com.project.booking.service.finance.checkin;

import com.project.booking.client.TourNfcCardClient;
import com.project.booking.dto.finance.PassengerCheckInRequest;
import com.project.booking.exception.AppException;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingPassengerStatus;
import com.project.booking.repository.BookingPassengerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.service.account.OnboardAccountService;

import java.time.LocalDateTime;

@Service
public class CheckPassengerServiceImpl implements CheckPassengerService {

    private final OnboardAccountService accounts;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final TourNfcCardClient tourNfcCardClient;

    public CheckPassengerServiceImpl(
            BookingPassengerRepository bookingPassengerRepository,
            TourNfcCardClient tourNfcCardClient, OnboardAccountService accounts) {
        this.accounts = accounts;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.tourNfcCardClient = tourNfcCardClient;
    }

    @Override
    @Transactional
    public void processSinglePassengerCheckIn(
            Long bookingId,
            PassengerCheckInRequest request) {

        if (request == null || request.getPassengerId() == null) {
            throw new AppException(
                    "Thông tin hành khách không hợp lệ!",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getNfcCode() == null || request.getNfcCode().isBlank()) {
            throw new AppException(
                    "Vui lòng chọn thẻ NFC!",
                    HttpStatus.BAD_REQUEST);
        }

        System.out.println("========================================");
        System.out.println("[CHECK-IN] Passenger ID: " + request.getPassengerId());
        System.out.println("[CHECK-IN] Booking ID: " + bookingId);
        System.out.println("[CHECK-IN] Room ID: " + request.getRoomId());
        System.out.println("[CHECK-IN] NFC UID: " + request.getNfcCode());
        System.out.println("========================================");

        BookingPassenger passengerAssignment = bookingPassengerRepository
                .findByBooking_IdAndPassenger_Id(
                        bookingId,
                        request.getPassengerId())
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy hành khách trong đơn đặt tour này!",
                        HttpStatus.NOT_FOUND));

        // Không gán thẻ khác cho hành khách đã check-in.
        if (passengerAssignment.getStatus() == BookingPassengerStatus.CHECKED_IN) {
            throw new AppException(
                    "Hành khách đã check-in trước đó!",
                    HttpStatus.CONFLICT);
        }

        // Gán thẻ ở tour-service trước khi cập nhật booking database.
        // Nếu tour-service từ chối, booking không được đánh dấu CHECKED_IN.
        tourNfcCardClient.assignCard(request.getNfcCode().trim());

        passengerAssignment.setRoomId(request.getRoomId());
        passengerAssignment.setNfcCardUid(request.getNfcCode().trim());
        passengerAssignment.setStatus(BookingPassengerStatus.CHECKED_IN);
        passengerAssignment.setCheckedInAt(LocalDateTime.now());

        bookingPassengerRepository.save(passengerAssignment);
        accounts.openForCheckIn(passengerAssignment.getBooking());

        System.out.println("[CHECK-IN] Check-in thành công.");
    }
}
