package com.project.tour.dto.passenger.cruise;

import java.util.UUID;

public record PassengerCruiseDTO(
        UUID id,
        String name,
        String code,
        String description,
        Integer maxPassengers,
        String imageUrl) {
}