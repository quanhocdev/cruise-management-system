package com.project.booking.controller.feedback;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/bookings")
public class FeedbackEligibilityController {
    private final JdbcTemplate jdbc;
    private final String apiKey;

    public FeedbackEligibilityController(JdbcTemplate jdbc, @Value("${INTERNAL_API_KEY:}") String apiKey) {
        this.jdbc = jdbc;
        this.apiKey = apiKey;
    }

    public record Eligibility(Long bookingId, UUID tourId, Long passengerVoyageId,
                              boolean participated, Long bookingPassengerId) {}

    public record Guest(Long bookingPassengerId, String name, boolean participated) {}
    public record Guests(Long bookingId, UUID tourId, java.util.List<Guest> passengers) {}

    @GetMapping("/{id}/feedback-passengers")
    public Guests passengers(@PathVariable Long id, @RequestParam UUID userId,
            @RequestHeader(value = "X-Internal-Api-Key", defaultValue = "") String supplied) {
        authorize(supplied);
        var bookings = jdbc.queryForList("SELECT tour_id,status FROM booking.bookings WHERE id=? AND created_by_user_id=?",id,userId);
        if (bookings.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean confirmed="CONFIRMED".equals(bookings.get(0).get("status"));
        var guests=jdbc.query("""
            select bp.id,p.full_name,bp.status from booking.booking_passengers bp
            join booking.passengers p on p.id=bp.passenger_id where bp.booking_id=? order by bp.id
            """,(rs,n)->new Guest(rs.getLong("id"),rs.getString("full_name"),confirmed && "CHECKED_OUT".equals(rs.getString("status"))),id);
        return new Guests(id,(UUID)bookings.get(0).get("tour_id"),guests);
    }

    private void authorize(String supplied) {
        if (apiKey.isBlank() || !MessageDigest.isEqual(apiKey.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/{id}/feedback-eligibility")
    public Eligibility eligibility(@PathVariable Long id, @RequestParam UUID userId,
            @RequestParam(required = false) Long bookingPassengerId,
            @RequestHeader(value = "X-Internal-Api-Key", defaultValue = "") String supplied) {
        authorize(supplied);
        var bookings = jdbc.queryForList(
            "SELECT tour_id,status FROM booking.bookings WHERE id=? AND created_by_user_id=?", id, userId);
        if (bookings.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var passengers = jdbc.queryForList(
            "SELECT id,status FROM booking.booking_passengers WHERE booking_id=? ORDER BY id", id);
        if (bookingPassengerId != null)
            passengers = passengers.stream().filter(p -> bookingPassengerId.equals(p.get("id"))).toList();
        if (passengers.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (passengers.size() != 1)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "BOOKING_PASSENGER_REQUIRED");
        var passenger = passengers.get(0);
        boolean participated = "CONFIRMED".equals(bookings.get(0).get("status"))
            && "CHECKED_OUT".equals(passenger.get("status"));
        return new Eligibility(id, (UUID) bookings.get(0).get("tour_id"), null,
            participated, (Long) passenger.get("id"));
    }
}
