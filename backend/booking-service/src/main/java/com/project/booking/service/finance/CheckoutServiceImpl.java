package com.project.booking.service.finance;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.dto.finance.CheckoutPreviewResponse.PassengerCheckoutPreview;
import com.project.booking.dto.finance.CheckoutPreviewResponse.UsageItem;
import com.project.booking.mapper.CheckoutPreviewMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.repository.ServiceUsageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final ActivityVisitUsageRepository activityVisitUsageRepository;
    private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
    private final ServiceUsageRepository serviceUsageRepository;
    private final ProductUsageRepository productUsageRepository;
    private final CheckoutPreviewMapper checkoutPreviewMapper;

    public CheckoutServiceImpl(
            BookingRepository bookingRepository,
            BookingPassengerRepository bookingPassengerRepository,
            ActivityVisitUsageRepository activityVisitUsageRepository,
            ActivityCruiseUsageRepository activityCruiseUsageRepository,
            ServiceUsageRepository serviceUsageRepository,
            ProductUsageRepository productUsageRepository,
            CheckoutPreviewMapper checkoutPreviewMapper) {

        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.activityVisitUsageRepository = activityVisitUsageRepository;
        this.activityCruiseUsageRepository = activityCruiseUsageRepository;
        this.serviceUsageRepository = serviceUsageRepository;
        this.productUsageRepository = productUsageRepository;
        this.checkoutPreviewMapper = checkoutPreviewMapper;
    }

    @Override
    public CheckoutPreviewResponse getCheckoutPreview(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking không tồn tại: " + bookingId));

        List<BookingPassenger> bookingPassengers = bookingPassengerRepository
                .findAllByBooking_IdOrderByIdAsc(bookingId);

        List<PassengerCheckoutPreview> passengers = bookingPassengers.stream()
                .map(this::buildPassengerPreview)
                .toList();

        BigDecimal grandTotal = passengers.stream()
                .map(PassengerCheckoutPreview::passengerTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CheckoutPreviewResponse(
                booking.getId(),
                booking.getBookingCode(),
                booking.getPrimaryContactName(),
                booking.getPrimaryContactEmail(),
                passengers,
                grandTotal);
    }

    private PassengerCheckoutPreview buildPassengerPreview(
            BookingPassenger bookingPassenger) {

        Long bookingPassengerId = bookingPassenger.getId();

        List<UsageItem> activityVisitUsages = activityVisitUsageRepository
                .findAllByBookingPassengerIdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(checkoutPreviewMapper::toUsageItem)
                .toList();

        List<UsageItem> activityCruiseUsages = activityCruiseUsageRepository
                .findAllByBookingPassengerIdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(checkoutPreviewMapper::toUsageItem)
                .toList();

        List<UsageItem> serviceUsages = serviceUsageRepository
                .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(checkoutPreviewMapper::toUsageItem)
                .toList();

        List<UsageItem> productUsages = productUsageRepository
                .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(checkoutPreviewMapper::toUsageItem)
                .toList();

        BigDecimal passengerTotal = calculateTotal(activityVisitUsages)
                .add(calculateTotal(activityCruiseUsages))
                .add(calculateTotal(serviceUsages))
                .add(calculateTotal(productUsages));

        Long passengerId = bookingPassenger
                .getPassenger()
                .getId();

        return new PassengerCheckoutPreview(
                bookingPassengerId,
                passengerId,
                activityVisitUsages,
                activityCruiseUsages,
                serviceUsages,
                productUsages,
                passengerTotal);
    }

    private BigDecimal calculateTotal(List<UsageItem> usages) {

        return usages.stream()
                .map(UsageItem::finalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}