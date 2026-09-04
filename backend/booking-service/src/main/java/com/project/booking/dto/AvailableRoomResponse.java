package com.project.booking.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AvailableRoomResponse(
    UUID roomId, String roomCode,
    UUID deckId, Integer deckNumber,
    UUID roomTypeId, String roomTypeName, String roomTypeDescription,
    BigDecimal price, Integer capacity,
    long occupiedCapacity, long remainingCapacity, boolean available
) {}
