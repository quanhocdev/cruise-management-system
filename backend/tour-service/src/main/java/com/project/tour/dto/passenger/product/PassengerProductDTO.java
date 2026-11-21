package com.project.tour.dto.passenger.product;

import java.math.BigDecimal;
import java.util.UUID;

public record PassengerProductDTO(
        UUID id,
        UUID productTourId,
        UUID cruiseAreaId,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String imageUrl) {
}