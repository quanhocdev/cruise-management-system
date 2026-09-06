package com.project.booking.service;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.exception.BookingException;
import com.project.booking.mapper.PassengerMapper;
import com.project.booking.model.Passenger;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final PassengerMapper passengerMapper;

    public PassengerServiceImpl(PassengerRepository passengerRepository,
            BookingPassengerRepository bookingPassengerRepository,
            PassengerMapper passengerMapper) {
        this.passengerRepository = passengerRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.passengerMapper = passengerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerResponse> getAllPassengers(Long userId) {
        if (userId != null) {
            return passengerRepository.findAll().stream()
                    .filter(p -> userId.equals(p.getUserId()))
                    .map(passengerMapper::toResponse)
                    .toList();
        }
        return passengerRepository.findAll().stream()
                .map(passengerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponse getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Passenger not found: " + id));
        return passengerMapper.toResponse(passenger);
    }

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerRequest request, Long userId) {
        Passenger passenger = passengerMapper.toEntity(request);
        passenger.setUserId(userId);
        Passenger saved = passengerRepository.save(passenger);
        return passengerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PassengerResponse updatePassenger(Long id, PassengerRequest request) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new BookingException(HttpStatus.NOT_FOUND, "Passenger not found: " + id));

        // Ràng buộc: Chỉ được đổi thông tin khi hành khách thuộc đơn hàng đang ở trạng
        // thái CONFIRMED
        boolean isConfirmed = bookingPassengerRepository.existsByPassenger_IdAndBooking_Status(id,
                com.project.booking.model.enums.BookingStatus.CONFIRMED);

        if (!isConfirmed) {
            throw new BookingException(HttpStatus.CONFLICT,
                    "Passenger information can only be modified when attached to a CONFIRMED booking");
        }

        passenger.setFullName(request.fullName().trim());
        passenger.setDateOfBirth(request.dateOfBirth());
        passenger.setGender(request.gender().trim());
        passenger.setPhoneNumber(request.phoneNumber());
        passenger.setEmail(request.email());
        passenger.setIdCardType(request.idCardType());
        passenger.setIdentificationNumber(request.identificationNumber().trim());
        passenger.setDocumentNote(request.documentNote());
        passenger.setIdCardImageUrl(request.idCardImageUrl());

        Passenger updated = passengerRepository.save(passenger);
        return passengerMapper.toResponse(updated);
    }
}