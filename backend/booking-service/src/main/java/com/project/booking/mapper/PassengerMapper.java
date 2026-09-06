package com.project.booking.mapper;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public Passenger toEntity(PassengerRequest request) {
        if (request == null) {
            return null;
        }
        Passenger passenger = new Passenger();
        passenger.setFullName(request.fullName().trim());
        passenger.setDateOfBirth(request.dateOfBirth());
        passenger.setGender(request.gender().trim());
        passenger.setPhoneNumber(request.phoneNumber());
        passenger.setEmail(request.email());
        passenger.setIdCardType(request.idCardType());
        passenger.setIdentificationNumber(request.identificationNumber().trim());
        passenger.setDocumentNote(request.documentNote());
        passenger.setIdCardImageUrl(request.idCardImageUrl());
        return passenger;
    }

    public PassengerResponse toResponse(Passenger passenger) {
        if (passenger == null) {
            return null;
        }
        return new PassengerResponse(
                passenger.getId(),
                passenger.getUserId(),
                passenger.getFullName(),
                passenger.getDateOfBirth(),
                passenger.getGender(),
                passenger.getPhoneNumber(),
                passenger.getEmail(),
                passenger.getIdCardType(),
                passenger.getIdentificationNumber(),
                passenger.getDocumentNote(),
                passenger.getIdCardImageUrl(),
                passenger.getCreatedAt(),
                passenger.getUpdatedAt());
    }
}