package com.project.booking.service.finance;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.dto.finance.CheckoutPreviewResponse.PassengerCheckoutPreview;
import com.project.booking.dto.finance.CheckoutPreviewResponse.UsageItem;
import com.project.booking.dto.finance.CheckoutResponse;
import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;
import com.project.booking.mapper.CheckoutMapper;
import com.project.booking.mapper.CheckoutPreviewMapper;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BillItemRepository;
import com.project.booking.repository.BillRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.repository.ServiceUsageRepository;
import com.project.booking.service.PaymentCheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final ActivityVisitUsageRepository activityVisitUsageRepository;
    private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
    private final ServiceUsageRepository serviceUsageRepository;
    private final ProductUsageRepository productUsageRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final CheckoutPreviewMapper checkoutPreviewMapper;
    private final CheckoutMapper checkoutMapper;
    private final PaymentCheckoutService paymentCheckoutService;

    public CheckoutServiceImpl(
            BookingRepository bookingRepository,
            BookingPassengerRepository bookingPassengerRepository,
            ActivityVisitUsageRepository activityVisitUsageRepository,
            ActivityCruiseUsageRepository activityCruiseUsageRepository,
            ServiceUsageRepository serviceUsageRepository,
            ProductUsageRepository productUsageRepository,
            BillRepository billRepository,
            BillItemRepository billItemRepository,
            CheckoutPreviewMapper checkoutPreviewMapper,
            CheckoutMapper checkoutMapper,
            PaymentCheckoutService paymentCheckoutService) {

        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.activityVisitUsageRepository = activityVisitUsageRepository;
        this.activityCruiseUsageRepository = activityCruiseUsageRepository;
        this.serviceUsageRepository = serviceUsageRepository;
        this.productUsageRepository = productUsageRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.checkoutPreviewMapper = checkoutPreviewMapper;
        this.checkoutMapper = checkoutMapper;
        this.paymentCheckoutService = paymentCheckoutService;
    }

    @Override
    public CheckoutPreviewResponse getCheckoutPreview(Long bookingId) {

        Booking booking = getBooking(bookingId);

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

    @Override
    @Transactional
    public CheckoutResponse confirmCheckout(Long bookingId) {

        Booking booking = getBooking(bookingId);

        List<BookingPassenger> bookingPassengers = bookingPassengerRepository
                .findAllByBooking_IdOrderByIdAsc(bookingId);

        BigDecimal grandTotal = BigDecimal.ZERO;

        Bill bill = new Bill();
        bill.setBooking(booking);
        bill.setBillCode(generateBillCode());
        bill.setTotalAmount(BigDecimal.ZERO);

        Bill savedBill = billRepository.save(bill);

        for (BookingPassenger bookingPassenger : bookingPassengers) {

            Long bookingPassengerId = bookingPassenger.getId();

            List<ActivityVisitUsage> activityVisitUsages = activityVisitUsageRepository
                    .findAllByBookingPassengerIdOrderByUsedAtDesc(
                            bookingPassengerId);

            for (ActivityVisitUsage usage : activityVisitUsages) {
                BillItem item = createBillItem(
                        savedBill,
                        bookingPassengerId,
                        "ACTIVITY_VISIT",
                        usage.getId(),
                        usage.getQuantity(),
                        usage.getUnitPrice(),
                        usage.getDiscountAmount(),
                        usage.getFinalAmount());

                billItemRepository.save(item);
                grandTotal = grandTotal.add(usage.getFinalAmount());
            }

            List<ActivityCruiseUsage> activityCruiseUsages = activityCruiseUsageRepository
                    .findAllByBookingPassengerIdOrderByUsedAtDesc(
                            bookingPassengerId);

            for (ActivityCruiseUsage usage : activityCruiseUsages) {
                BillItem item = createBillItem(
                        savedBill,
                        bookingPassengerId,
                        "ACTIVITY_CRUISE",
                        usage.getId(),
                        usage.getQuantity(),
                        usage.getUnitPrice(),
                        usage.getDiscountAmount(),
                        usage.getFinalAmount());

                billItemRepository.save(item);
                grandTotal = grandTotal.add(usage.getFinalAmount());
            }

            List<ServiceUsage> serviceUsages = serviceUsageRepository
                    .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                            bookingPassengerId);

            for (ServiceUsage usage : serviceUsages) {
                BillItem item = createBillItem(
                        savedBill,
                        bookingPassengerId,
                        "SERVICE",
                        usage.getId(),
                        1,
                        usage.getUnitPrice(),
                        usage.getDiscountAmount(),
                        usage.getFinalAmount());

                billItemRepository.save(item);
                grandTotal = grandTotal.add(usage.getFinalAmount());
            }

            List<ProductUsage> productUsages = productUsageRepository
                    .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                            bookingPassengerId);

            for (ProductUsage usage : productUsages) {
                BillItem item = createBillItem(
                        savedBill,
                        bookingPassengerId,
                        "PRODUCT",
                        usage.getId(),
                        usage.getQuantity(),
                        usage.getUnitPrice(),
                        usage.getDiscountAmount(),
                        usage.getFinalAmount());

                billItemRepository.save(item);
                grandTotal = grandTotal.add(usage.getFinalAmount());
            }
        }

        savedBill.setTotalAmount(grandTotal);

        Bill finalBill = billRepository.save(savedBill);

        /*
         * Bill đã được tạo.
         * Bây giờ booking-service gọi payment-service qua REST
         * để tạo Payment và lấy VNPay paymentUrl.
         */
        BillPaymentRequest paymentRequest = new BillPaymentRequest(
                finalBill.getId(),
                booking.getCreatedByUserId(),
                finalBill.getTotalAmount());

        BillPaymentResponse paymentResponse = paymentCheckoutService.createPayment(paymentRequest);

        List<BillItem> items = billItemRepository
                .findAllByBill_IdOrderByIdAsc(finalBill.getId());

        CheckoutResponse checkoutResponse = checkoutMapper.toResponse(finalBill, items);

        return new CheckoutResponse(
                checkoutResponse.billId(),
                checkoutResponse.billCode(),
                checkoutResponse.bookingId(),
                checkoutResponse.totalAmount(),
                checkoutResponse.createdAt(),
                checkoutResponse.items(),
                paymentResponse.paymentId(),
                paymentResponse.paymentUrl(),
                paymentResponse.status());
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

    private BillItem createBillItem(
            Bill bill,
            Long bookingPassengerId,
            String usageType,
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount) {

        BillItem item = new BillItem();

        item.setBill(bill);
        item.setBookingPassengerId(bookingPassengerId);
        item.setUsageType(usageType);
        item.setUsageId(usageId);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setDiscountAmount(discountAmount);
        item.setFinalAmount(finalAmount);

        return item;
    }

    private BigDecimal calculateTotal(List<UsageItem> usages) {

        return usages.stream()
                .map(UsageItem::finalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Booking getBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking không tồn tại: " + bookingId));
    }

    private String generateBillCode() {

        return "BILL-"
                + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}