package com.project.booking.dto.booking;

import com.project.booking.dto.passenger.PassengerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull(message = "Tour ID is required") UUID tourId,

        @NotNull(message = "Tour Package ID is required") UUID tourPackageId,

        @NotBlank(message = "Primary contact name is required") @Size(max = 150, message = "Contact name must be less than 150 characters") String primaryContactName,

        @NotBlank(message = "Primary contact phone is required") @Size(max = 30, message = "Contact phone must be less than 30 characters") String primaryContactPhone,

        @NotEmpty(message = "Passenger list cannot be empty") @Valid List<PassengerRequest> passengers) {
    public CreateBookingRequest {
    }
}