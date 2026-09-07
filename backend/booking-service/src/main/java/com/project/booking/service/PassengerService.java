package com.project.booking.service;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import java.util.List;

public interface PassengerService {
    List<PassengerResponse> getAllPassengers(Long userId);

    PassengerResponse getPassengerById(Long id);

    PassengerResponse updatePassenger(Long id, PassengerRequest request);
}