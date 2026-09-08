package com.project.booking.service;

import com.project.common.dto.UploadResult;
import com.project.common.event.BookingConfirmedEvent;
import com.project.common.event.BookingCreatedEvent;
import com.project.common.service.file.FileStorageService;
import com.project.booking.dto.booking.*;
import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.AvailableRoomResponse;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.BookingMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.Passenger;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.PassengerRepository;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final String BOOKING_CREATED_TOPIC = "booking-created-topic";

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookingMapper bookingMapper;
    private final FileStorageService fileStorageService;
    private final KafkaTemplate<String, Object> kafkaTemplate; // Khai báo KafkaTemplate

    public BookingServiceImpl(BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            BookingMapper bookingMapper,
            FileStorageService fileStorageService,
            KafkaTemplate<String, Object> kafkaTemplate) { // Inject vào Constructor
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.bookingMapper = bookingMapper;
        this.fileStorageService = fileStorageService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public synchronized BookingResponse create(CreateBookingRequest request, Long userId, String email) {
        try {
            System.out.println(">>> [SERVICE] Bắt đầu xử lý create booking...");

            BigDecimal unitPrice = request.getUnitPrice() != null
                    ? request.getUnitPrice()
                    : BigDecimal.ZERO;

            int passengerCount = request.getPassengers().size();
            BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(passengerCount));

            Booking booking = new Booking();
            booking.setCreatedByUserId(userId);
            booking.setPrimaryContactEmail(email);
            booking.setTourId(UUID.fromString(request.getTourId()));
            booking.setTourPackageId(UUID.fromString(request.getTourPackageId()));
            booking.setBookingCode(generateBookingCode());
            booking.setNumberPassengers(passengerCount);
            booking.setPrimaryContactName(request.getPrimaryContactName().trim());
            booking.setPrimaryContactPhone(request.getPrimaryContactPhone().trim());
            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.PENDING_PAYMENT);

            Booking savedBooking = bookingRepository.save(booking);
            System.out.println(">>> [SERVICE] Đã lưu xong Booking ID: " + savedBooking.getId());

            for (int i = 0; i < request.getPassengers().size(); i++) {
                PassengerRequest pReq = request.getPassengers().get(i);
                System.out.println(">>> [SERVICE] Đang xử lý hành khách thứ " + (i + 1) + ": " + pReq.getFullName());

                Passenger passenger = new Passenger();
                passenger.setBooking(savedBooking);
                passenger.setFullName(pReq.getFullName() != null ? pReq.getFullName().trim() : null);
                passenger.setDateOfBirth(pReq.getDateOfBirth());
                passenger.setGender(pReq.getGender() != null ? pReq.getGender().trim() : null);
                passenger.setPhoneNumber(pReq.getPhoneNumber());
                passenger.setEmail(pReq.getEmail());
                passenger.setIdCardType(pReq.getIdCardType());
                passenger.setIdentificationNumber(
                        pReq.getIdentificationNumber() != null ? pReq.getIdentificationNumber().trim() : null);
                passenger.setDocumentNote(pReq.getDocumentNote());

                if (pReq.getIdCardImage() != null && !pReq.getIdCardImage().isEmpty()) {
                    System.out.println(">>> [SERVICE] Đang upload ảnh cho hành khách " + pReq.getFullName());
                    UploadResult uploadResult = fileStorageService.saveMultipart(
                            pReq.getIdCardImage(),
                            "passengers");
                    passenger.setIdCardImageUrl(uploadResult.getUrl());
                    passenger.setIdCardImagePublicId(uploadResult.getPublicId());
                }

                Passenger savedPassenger = passengerRepository.save(passenger);

                BookingPassenger link = new BookingPassenger();
                link.setBooking(savedBooking);
                link.setPassenger(savedPassenger);
                link.setCheckinStatus("PENDING");
                bookingPassengerRepository.save(link);
            }

            // =========================================================
            // BẮN KAFKA EVENT SAU KHI LƯU XONG ĐƠN HÀNG VÀ HÀNH KHÁCH
            // =========================================================
            BookingCreatedEvent event = new BookingCreatedEvent(
                    savedBooking.getId(),
                    userId,
                    savedBooking.getTourId(),
                    savedBooking.getTourPackageId(),
                    passengerCount,
                    savedBooking.getTotalAmount(),
                    LocalDateTime.now());

            // Gửi đi với key là bookingId dạng String để phân vùng (partition) rõ ràng
            kafkaTemplate.send(BOOKING_CREATED_TOPIC, savedBooking.getId().toString(), event);
            System.out.println(
                    ">>> [KAFKA PRODUCER] Đã bắn event PENDING_PAYMENT cho Booking ID: " + savedBooking.getId());

            List<BookingPassenger> links = bookingPassengerRepository
                    .findAllByBooking_IdOrderByIdAsc(savedBooking.getId());
            return bookingMapper.toResponse(savedBooking, links);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse get(Long id, Long requesterId, boolean privileged) {
        Booking booking = findBookingById(id);
        if (!privileged && !Objects.equals(booking.getCreatedByUserId(), requesterId)) {
            throw new AppException("You cannot access this booking", HttpStatus.FORBIDDEN);
        }
        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(booking.getId());
        return bookingMapper.toResponse(booking, links);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMine(Long userId) {
        return bookingRepository.findAllByCreatedByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(booking -> {
                    List<BookingPassenger> links = bookingPassengerRepository
                            .findAllByBooking_IdOrderByIdAsc(booking.getId());
                    return bookingMapper.toResponse(booking, links);
                })
                .toList();
    }

    @Override
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = findBookingById(id);
        if (!Objects.equals(booking.getCreatedByUserId(), userId)) {
            throw new AppException("You cannot cancel this booking", HttpStatus.FORBIDDEN);
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException("Only CONFIRMED bookings can be cancelled", HttpStatus.CONFLICT);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        List<BookingPassenger> links = bookingPassengerRepository.findAllByBooking_IdOrderByIdAsc(saved.getId());
        return bookingMapper.toResponse(saved, links);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableRoomResponse> getAvailableRooms(UUID tourId, UUID tourPackageId) {
        return List.of();
    }

    @Override
    public int sendDepartureReminders(LocalDate departureDate) {
        return 0;
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new AppException("Booking not found: " + id, HttpStatus.NOT_FOUND));
    }

    private String generateBookingCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String code = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (!bookingRepository.existsByBookingCode(code)) {
                return code;
            }
        }
        throw new AppException("Cannot generate a unique booking code", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Transactional
    public void processPaymentSuccess(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            System.out.println(">>> [SERVICE] Không tìm thấy Booking ID: " + bookingId);
            return;
        }

        if (booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
            booking.setStatus(BookingStatus.CONFIRMED);
            Booking savedBooking = bookingRepository.save(booking);

            System.out.println(">>> [SERVICE] Đã cập nhật đơn hàng #" + bookingId + " thành CONFIRMED.");

            // Bắn Kafka Event sang notification-service
            BookingConfirmedEvent confirmedEvent = new BookingConfirmedEvent(
                    savedBooking.getCreatedByUserId(),
                    savedBooking.getPrimaryContactEmail(),
                    savedBooking.getPrimaryContactName(),
                    savedBooking.getBookingCode(),
                    savedBooking.getNumberPassengers(),
                    savedBooking.getTotalAmount());

            kafkaTemplate.send("booking-confirmed-topic", savedBooking.getBookingCode(), confirmedEvent);
            System.out.println(
                    ">>> [KAFKA PRODUCER] Đã bắn event BookingConfirmedEvent cho mã: " + savedBooking.getBookingCode());
        } else {
            System.out.println(">>> [SERVICE] Đơn hàng #" + bookingId + " đã ở trạng thái: " + booking.getStatus());
        }
    }
}