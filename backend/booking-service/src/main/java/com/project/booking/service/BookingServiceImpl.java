package com.project.booking.service;

import com.project.common.dto.UploadResult;
import com.project.common.event.BookingConfirmedEvent;
import com.project.common.event.BookingCreatedEvent;
import com.project.common.service.file.FileStorageService;
import com.project.booking.dto.booking.*;
import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.BookingMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.Passenger;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.InfoTourPackage;
import com.project.booking.model.enums.BookingPassengerStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.PassengerRepository;
import com.project.booking.service.redis.TourRedisService;
import com.project.booking.repository.BookingPassengerRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.booking.repository.InfoTourPackageRepository;
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
    private final InfoTourPackageRepository infoTourPackageRepository;
    private final BookingMapper bookingMapper;
    private final FileStorageService fileStorageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TourRedisService tourRedisService;

    public BookingServiceImpl(BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            InfoTourPackageRepository infoTourPackageRepository,
            BookingMapper bookingMapper,
            FileStorageService fileStorageService,
            KafkaTemplate<String, Object> kafkaTemplate,
            TourRedisService tourRedisService) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.infoTourPackageRepository = infoTourPackageRepository;
        this.bookingMapper = bookingMapper;
        this.fileStorageService = fileStorageService;
        this.kafkaTemplate = kafkaTemplate;
        this.tourRedisService = tourRedisService;
    }

    @Override
    public synchronized BookingResponse create(CreateBookingRequest request, Long userId, String email) {
        try {
            System.out.println(">>> [SERVICE] Bắt đầu xử lý create booking theo kho phòng...");

            UUID packageId = UUID.fromString(request.getTourPackageId());
            int requestedRooms = request.getNumberOfRooms() != null ? request.getNumberOfRooms() : 1;
            int passengerCount = request.getPassengers().size();

            // 1. Lấy thông tin gói tour từ mirror table (InfoTourPackage)
            InfoTourPackage pkg = infoTourPackageRepository.findById(packageId)
                    .orElseThrow(() -> new AppException("Không tìm thấy gói tour hợp lệ trong hệ thống!",
                            HttpStatus.BAD_REQUEST));

            // 2. Kiểm tra sức chứa hành khách trên số lượng phòng đặt
            // Giả sử mỗi phòng cho phép tối đa maxPassengers (hoặc mặc định 2-3 khách/phòng
            // tùy logic hệ thống)
            int maxPassengersPerRoom = pkg.getMaxPassengers() != null && pkg.getMaxPassengers() > 0
                    ? pkg.getMaxPassengers()
                    : 2; // Mặc định mỗi phòng tối đa 2 khách nếu chưa cấu hình

            int totalAllowedPassengers = maxPassengersPerRoom * requestedRooms;
            if (passengerCount > totalAllowedPassengers) {
                throw new AppException(
                        String.format(
                                "Số lượng hành khách (%d người) vượt quá sức chứa tối đa của số phòng đã chọn (%d phòng tối đa %d khách). Vui lòng đăng ký thêm phòng hoặc chọn gói phòng lớn hơn!",
                                passengerCount, requestedRooms, totalAllowedPassengers),
                        HttpStatus.BAD_REQUEST);
            }

            // 3. Trừ kho phòng đồng thời trên Redis qua Lua script
            Long redisResult = tourRedisService.reservePackageRooms(packageId, requestedRooms);
            if (redisResult == null || redisResult == -1) {
                throw new AppException("Kho phòng của gói tour này chưa được khởi tạo hoặc không tồn tại!",
                        HttpStatus.BAD_REQUEST);
            }
            if (redisResult == -2) {
                throw new AppException("Rất tiếc, số lượng phòng trống của gói này không đủ đáp ứng yêu cầu!",
                        HttpStatus.BAD_REQUEST);
            }

            // 4. Tính tổng tiền = Giá gói * Số lượng phòng
            BigDecimal packagePrice = pkg.getPrice();
            BigDecimal totalAmount = packagePrice.multiply(BigDecimal.valueOf(requestedRooms));

            // 5. Lưu thông tin Booking
            Booking booking = new Booking();
            booking.setCreatedByUserId(userId);
            booking.setPrimaryContactEmail(email);
            booking.setTourId(UUID.fromString(request.getTourId()));
            booking.setTourPackageId(packageId);
            booking.setBookingCode(generateBookingCode());
            booking.setNumberOfRooms(requestedRooms);
            booking.setNumberPassengers(passengerCount);
            booking.setPrimaryContactName(request.getPrimaryContactName().trim());
            booking.setPrimaryContactPhone(request.getPrimaryContactPhone().trim());
            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.PENDING_PAYMENT);

            Booking savedBooking = bookingRepository.save(booking);
            System.out.println(">>> [SERVICE] Đã lưu xong Booking ID: " + savedBooking.getId());

            // 6. Lưu danh sách hành khách
            for (int i = 0; i < request.getPassengers().size(); i++) {
                PassengerRequest pReq = request.getPassengers().get(i);
                Passenger passenger = new Passenger();
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
                link.setStatus(BookingPassengerStatus.PENDING);
                bookingPassengerRepository.save(link);
            }

            // 7. Bắn Kafka Event
            BookingCreatedEvent event = new BookingCreatedEvent(
                    savedBooking.getId(),
                    userId,
                    savedBooking.getTourId(),
                    savedBooking.getTourPackageId(),
                    passengerCount,
                    savedBooking.getTotalAmount(),
                    LocalDateTime.now());

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