package com.project.booking.service.finance;

import com.project.booking.dto.booking.BookingSummaryResponse;
import com.project.booking.dto.passenger.BookingPassengerDetailResponse;
import com.project.booking.mapper.BookingMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FinanceBookingServiceImpl implements FinanceBookingService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookingMapper bookingMapper; // Inject Mapper vào đây

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
        List<Booking> bookings = bookingRepository.findAllByTourId(tourId);

        // Gọi thẳng mapper thay vì tự new DTO thủ công trong service
        return bookings.stream()
                .map(bookingMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public List<BookingPassengerDetailResponse> getPassengersByBookingId(Long bookingId) {
        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(bookingId);

        // Gọi thẳng mapper để xử lý chuyển đổi dữ liệu phức tạp
        return links.stream()
                .map(bookingMapper::toPassengerDetailResponse)
                .toList();
    }
}