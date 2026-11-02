package com.project.booking.service.passenger;

import java.util.UUID;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import java.util.List;

public interface PassengerService {
    List<PassengerResponse> getAllPassengers(UUID userId);

    PassengerResponse getPassengerById(Long id);

    PassengerResponse updatePassenger(Long id, PassengerRequest request);
}