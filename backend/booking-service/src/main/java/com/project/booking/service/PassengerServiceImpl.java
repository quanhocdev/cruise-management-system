package com.project.booking.service;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.exception.AppException;
import com.project.booking.mapper.PassengerMapper;
import com.project.booking.model.Passenger;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final PassengerMapper passengerMapper;
    private final FileStorageService fileStorageService;

    public PassengerServiceImpl(PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            PassengerMapper passengerMapper,
            FileStorageService fileStorageService) {
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.passengerMapper = passengerMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerResponse> getAllPassengers(Long userId) {
        if (userId == null) {
            return List.of();
        }

        // Lấy danh sách hành khách thông qua các đơn đặt vé (Booking) do user này tạo
        // (createdByUserId)
        return bookingPassengerRepository.findByBooking_CreatedByUserId(userId).stream()
                .map(bp -> bp.getPassenger())
                .distinct() // Tránh trùng lặp nếu hành khách đi nhiều tour khác nhau
                .map(passengerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponse getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new AppException("Passenger not found: " + id, HttpStatus.NOT_FOUND));
        return passengerMapper.toResponse(passenger);
    }

    @Override
    public PassengerResponse updatePassenger(Long id, PassengerRequest request) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new AppException("Passenger not found: " + id, HttpStatus.NOT_FOUND));

        boolean isConfirmed = bookingPassengerRepository.existsByPassenger_IdAndBooking_Status(id,
                BookingStatus.CONFIRMED);

        if (!isConfirmed) {
            throw new AppException("Passenger information can only be modified when attached to a CONFIRMED booking",
                    HttpStatus.CONFLICT);
        }

        passenger.setFullName(request.getFullName() != null ? request.getFullName().trim() : null);
        passenger.setDateOfBirth(request.getDateOfBirth());
        passenger.setGender(request.getGender() != null ? request.getGender().trim() : null);
        passenger.setPhoneNumber(request.getPhoneNumber());
        passenger.setEmail(request.getEmail());
        passenger.setIdCardType(request.getIdCardType());
        passenger.setIdentificationNumber(
                request.getIdentificationNumber() != null ? request.getIdentificationNumber().trim() : null);
        passenger.setDocumentNote(request.getDocumentNote());

        // Xử lý cập nhật ảnh mới nếu có gửi lên
        if (request.getIdCardImage() != null && !request.getIdCardImage().isEmpty()) {
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getIdCardImage(),
                    "passengers");
            passenger.setIdCardImageUrl(uploadResult.getUrl());
            passenger.setIdCardImagePublicId(uploadResult.getPublicId());
        }

        Passenger updated = passengerRepository.save(passenger);
        return passengerMapper.toResponse(updated);
    }
}