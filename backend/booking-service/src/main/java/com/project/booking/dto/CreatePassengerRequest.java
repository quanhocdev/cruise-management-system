package com.project.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePassengerRequest(
    Long userId,
    @NotBlank @Size(max = 150) String fullName,
    @NotNull @Past LocalDate dateOfBirth,
    @NotBlank @Size(max = 20) String gender,
    @Size(max = 30) String phoneNumber,
    @Email @Size(max = 255) String email,
    @NotNull UUID cabinId
) {}
