package com.project.booking.client;

import java.math.BigDecimal;
import java.util.UUID;

public record TourRoomContext(
    UUID roomId, String roomCode,
    UUID deckId, Integer deckNumber,
    UUID roomTypeId, String roomTypeName, String roomTypeDescription,
    BigDecimal price, Integer capacity
) {}
