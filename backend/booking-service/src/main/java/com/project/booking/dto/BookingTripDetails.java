package com.project.booking.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookingTripDetails(UUID voyageId, String tourName, String cruiseName,
        LocalDate startDate, LocalDate endDate, List<RoomDetails> rooms, List<ItineraryDay> itinerary, List<Activity> activities,
        List<CatalogItem> catalog) {
    public record CatalogItem(UUID id, String type, String name, String description, String imageUrl,
        java.math.BigDecimal price, String location, Integer durationMinutes, Integer maxPassengers, String status) {}
    public record Activity(UUID id, String type, String name, String description, java.time.LocalDateTime startTime,
        java.time.LocalDateTime endTime, String location, java.math.BigDecimal price, Integer maxPassengers, String status) {}
    public record ItineraryDay(UUID id, Integer dayNumber, LocalDate date, String name, String description) {}
    public record RoomDetails(UUID roomId, String roomCode, Integer deckNumber, String roomTypeName) {}
}
