package com.project.booking.controller;

import com.project.booking.client.TourClient;
import com.project.booking.dto.BookingTripDetails;
import com.project.booking.service.BookingService;
import com.project.booking.exception.BookingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingTripController {
    private final BookingService bookings;
    private final TourClient tours;
    public BookingTripController(BookingService bookings, TourClient tours) {
        this.bookings = bookings; this.tours = tours;
    }

    @GetMapping("/{id}/trip-details")
    public BookingTripDetails get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId;
        try { Object claim = jwt.getClaim("userId"); userId = Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new BookingException(HttpStatus.BAD_REQUEST, "Invalid userId"); }
        // Ownership is checked before making any request to tour-service.
        var booking = bookings.get(id, userId, false);
        var trip = tours.getTripDetails(booking.voyageId());
        Set<java.util.UUID> assignedRooms = booking.passengers().stream()
            .map(p -> p.cabinId()).filter(Objects::nonNull).collect(Collectors.toSet());
        return new BookingTripDetails(trip.voyageId(), trip.tourName(), trip.cruiseName(),
            trip.startDate(), trip.endDate(), trip.rooms() == null ? java.util.List.of() : trip.rooms().stream()
                .filter(r -> assignedRooms.contains(r.roomId())).toList(), trip.itinerary());
    }
}
