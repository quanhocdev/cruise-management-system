package com.project.booking.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookingTripDetails(UUID voyageId, String tourName, String cruiseName,
        LocalDate startDate, LocalDate endDate, List<RoomDetails> rooms) {
    public record RoomDetails(UUID roomId, String roomCode, Integer deckNumber, String roomTypeName) {}
}
