package com.project.tour.dto.passenger.route;

import java.math.BigDecimal;
import java.util.UUID;

public record PassengerPortDTO(
        UUID id,
        String name,
        String city,
        String country,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String description) {
}