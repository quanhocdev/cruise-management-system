package com.project.booking.service;

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
                .orElseThrow(() -> new AppException("Passenger not found: " + id, HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new AppException("Passenger not found: " + id, HttpStatus.NOT_FOUND));

        // Ràng buộc: Chỉ sửa thông tin khi hành khách thuộc đơn ở trạng thái CONFIRMED
        boolean isConfirmed = bookingPassengerRepository.existsByPassenger_IdAndBooking_Status(id,
                BookingStatus.CONFIRMED);

        if (!isConfirmed) {
            throw new AppException("Passenger information can only be modified when attached to a CONFIRMED booking",
                    HttpStatus.CONFLICT);
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