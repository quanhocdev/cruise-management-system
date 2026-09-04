package com.project.tour.dto.passenger;

import java.math.BigDecimal;
import java.util.UUID;

public record PassengerRoomCatalogResponse(
    UUID roomId, String roomCode,
    UUID deckId, Integer deckNumber,
    UUID roomTypeId, String roomTypeName, String roomTypeDescription,
    BigDecimal price, Integer capacity
) {}
