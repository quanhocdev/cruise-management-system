package com.project.booking.service;

import com.project.booking.dto.convenience.NfcResolveRequest;
import com.project.booking.dto.convenience.NfcResolveResponse;
import com.project.booking.exception.AppException;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.BookingPassengerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConvenienceNfcServiceImpl implements ConvenienceNfcService {

    private final BookingPassengerRepository bookingPassengerRepository;

    public ConvenienceNfcServiceImpl(
            BookingPassengerRepository bookingPassengerRepository) {

        this.bookingPassengerRepository = bookingPassengerRepository;
    }

    @Override
    public NfcResolveResponse resolve(NfcResolveRequest request) {

        String nfcCardUid = request.nfcCardUid().trim();

        BookingPassenger bookingPassenger = bookingPassengerRepository
                .findByNfcCardUid(nfcCardUid)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy hành khách được gán với NFC card: " + nfcCardUid,
                        HttpStatus.NOT_FOUND));

        Booking booking = bookingPassenger.getBooking();

        if (booking == null) {
            throw new AppException(
                    "NFC card chưa được liên kết với booking.",
                    HttpStatus.NOT_FOUND);
        }

        if (booking.getTourId() == null) {
            throw new AppException(
                    "Booking của hành khách chưa có tour.",
                    HttpStatus.CONFLICT);
        }

        String passengerName = bookingPassenger.getPassenger() != null
                ? bookingPassenger.getPassenger().getFullName()
                : null;

        return new NfcResolveResponse(
                bookingPassenger.getId(),
                passengerName,
                booking.getId(),
                booking.getTourId(),
                booking.getTourPackageId());
    }
}