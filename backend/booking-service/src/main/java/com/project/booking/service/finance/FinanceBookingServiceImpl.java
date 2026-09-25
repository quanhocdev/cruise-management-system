package com.project.booking.service.finance;

import com.project.booking.dto.booking.BookingSummaryResponse;
import com.project.booking.dto.passenger.BookingPassengerDetailResponse;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.BookingMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.BookingPassengerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FinanceBookingServiceImpl implements FinanceBookingService {

        private final BookingRepository bookingRepository;
        private final BookingPassengerRepository bookingPassengerRepository;
        private final BookingMapper bookingMapper;

        public FinanceBookingServiceImpl(
                        BookingRepository bookingRepository,
                        BookingPassengerRepository bookingPassengerRepository,
                        BookingMapper bookingMapper) {

                this.bookingRepository = bookingRepository;
                this.bookingPassengerRepository = bookingPassengerRepository;
                this.bookingMapper = bookingMapper;
        }

        @Override
        public List<BookingSummaryResponse> getBookingsByTourId(UUID tourId) {

                System.out.println("[FINANCE SERVICE] getBookingsByTourId()");
                System.out.println("[FINANCE SERVICE] tourId = " + tourId);

                System.out.println("[FINANCE SERVICE] Calling repository...");

                List<Booking> bookings = bookingRepository.findAllByTourId(tourId);

                System.out.println(
                                "[FINANCE SERVICE] Repository returned "
                                                + bookings.size()
                                                + " booking(s)");

                System.out.println("[FINANCE SERVICE] Mapping bookings...");

                List<BookingSummaryResponse> result = bookings.stream()
                                .map(booking -> {
                                        System.out.println(
                                                        "[FINANCE SERVICE] Mapping booking id = "
                                                                        + booking.getId());

                                        return bookingMapper.toSummaryResponse(booking);
                                })
                                .toList();

                System.out.println(
                                "[FINANCE SERVICE] Mapping completed. Result size = "
                                                + result.size());

                return result;
        }

        @Override
        public List<BookingPassengerDetailResponse> getPassengersByBookingId(
                        Long bookingId) {

                System.out.println("[FINANCE SERVICE] getPassengersByBookingId()");
                System.out.println("[FINANCE SERVICE] bookingId = " + bookingId);

                if (!bookingRepository.existsById(bookingId)) {
                        throw new AppException(
                                        "Không tìm thấy booking với ID: " + bookingId,
                                        HttpStatus.NOT_FOUND);
                }

                List<BookingPassenger> links = bookingPassengerRepository
                                .findAllByBooking_IdOrderByIdAsc(bookingId);

                System.out.println(
                                "[FINANCE SERVICE] Passenger links found = "
                                                + links.size());

                List<BookingPassengerDetailResponse> result = links.stream()
                                .map(bookingMapper::toPassengerDetailResponse)
                                .toList();

                System.out.println(
                                "[FINANCE SERVICE] Passenger mapping completed. Result size = "
                                                + result.size());

                return result;
        }
}