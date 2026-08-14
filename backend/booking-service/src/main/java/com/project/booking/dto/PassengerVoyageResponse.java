package com.project.booking.dto;

import com.project.booking.model.enums.*;
import java.time.LocalDate;
import java.util.UUID;

public record PassengerVoyageResponse(
    Long passengerVoyageId, Long passengerId, Long userId, String fullName,
    LocalDate dateOfBirth, String gender, String phoneNumber, String email,
    UUID cabinId, PassengerStatus passengerStatus, EmbarkationStatus embarkationStatus
) {}
