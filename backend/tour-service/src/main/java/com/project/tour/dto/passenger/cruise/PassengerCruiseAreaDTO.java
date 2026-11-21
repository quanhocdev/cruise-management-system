package com.project.tour.dto.passenger.cruise;

import java.util.UUID;

public record PassengerCruiseAreaDTO(
        UUID id,
        String name,
        String description,
        String imageUrl) {
}