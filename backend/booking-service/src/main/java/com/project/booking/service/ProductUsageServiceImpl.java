package com.project.booking.service;

import com.project.booking.dto.ProductUsageRequest;
import com.project.booking.dto.ProductUsageResponse;
import com.project.booking.mapper.ProductUsageMapper;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ProductUsage;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.ProductUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductUsageServiceImpl implements ProductUsageService {

    private final ProductUsageRepository productUsageRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final ProductUsageMapper productUsageMapper;

    public ProductUsageServiceImpl(
            ProductUsageRepository productUsageRepository,
            BookingPassengerRepository bookingPassengerRepository,
            ProductUsageMapper productUsageMapper) {

        this.productUsageRepository = productUsageRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.productUsageMapper = productUsageMapper;
    }

    @Override
    @Transactional
    public ProductUsageResponse create(ProductUsageRequest request) {

        BookingPassenger bookingPassenger = bookingPassengerRepository
                .findByNfcCardUid(request.nfcCardUid())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hành khách với NFC card UID: "
                                + request.nfcCardUid()));

        ProductUsage usage = new ProductUsage();

        usage.setBookingPassenger(bookingPassenger);
        usage.setProductTourId(request.productTourId());
        usage.setQuantity(request.quantity());

        // TODO:
        // 1. Gọi Tour Service lấy ProductTour
        // 2. Kiểm tra ProductTour thuộc tour của Booking
        // 3. Lấy Product price
        // 4. Lấy PackageBenefit
        // 5. Tính discountAmount
        // 6. Tính finalAmount

        BigDecimal unitPrice = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        BigDecimal finalAmount = unitPrice
                .multiply(BigDecimal.valueOf(request.quantity()))
                .subtract(discountAmount);

        usage.setUnitPrice(unitPrice);
        usage.setDiscountAmount(discountAmount);
        usage.setFinalAmount(finalAmount);
        usage.setUsedAt(LocalDateTime.now());

        ProductUsage savedUsage = productUsageRepository.save(usage);

        return productUsageMapper.toResponse(savedUsage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId) {

        return productUsageRepository
                .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                        bookingPassengerId)
                .stream()
                .map(productUsageMapper::toResponse)
                .toList();
    }
}