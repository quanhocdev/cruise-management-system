package com.project.tour.controller.passenger;

import com.project.tour.exception.AppException;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.model.enums.ScheduleStatus;
import com.project.tour.dto.passenger.PassengerItineraryDayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/voyages")
public class InternalTripDetailsController {
    private final TourRepository tours;
    private final RoomRepository rooms;
    private final ScheduleRepository schedules;
    private final com.project.tour.service.passenger.PassengerTripActivities activities;
    private final byte[] key;
    private final com.project.tour.service.passenger.PassengerTripCatalog catalog;

    public InternalTripDetailsController(TourRepository tours, RoomRepository rooms, ScheduleRepository schedules,
            com.project.tour.service.passenger.PassengerTripActivities activities,
            com.project.tour.service.passenger.PassengerTripCatalog catalog,
            @Value("${internal.api-key}") String key) {
        this.tours = tours; this.rooms = rooms;
        this.schedules = schedules;
        this.activities = activities;
        this.catalog = catalog;
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}/trip-details")
    @Transactional(readOnly = true)
    public TripDetails get(@PathVariable UUID id,
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String supplied) {
        if (!MessageDigest.isEqual(key, (supplied == null ? "" : supplied).getBytes(StandardCharsets.UTF_8)))
            throw new AppException("Invalid internal API key", HttpStatus.UNAUTHORIZED);
        var tour = tours.findById(id)
            .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));
        var cruise = tour.getCruise();
        // Existing bookings remain readable after sales close or rooms are deactivated.
        var details = cruise == null ? List.<RoomDetails>of() : rooms.findTripRoomsByCruiseId(cruise.getId())
            .stream().map(r -> new RoomDetails(r.getId(), r.getCode(),
                r.getCruiseDeck().getDeckNumber(), r.getRoomType().getName())).toList();
        return new TripDetails(id, tour.getName(), cruise == null ? null : cruise.getName(),
            tour.getStartDate(), tour.getEndDate(), details,
            schedules.findAllByTour_IdAndStatusOrderByDayNumberAsc(id, ScheduleStatus.ACTIVE).stream()
                .map(s -> new PassengerItineraryDayResponse(s.getId(), s.getDayNumber(), s.getRealDay(),
                    s.getName(), s.getDescription())).toList(), activities.get(id), catalog.get(id));
    }

    public record RoomDetails(UUID roomId, String roomCode, Integer deckNumber, String roomTypeName) {}
    public record TripDetails(UUID voyageId, String tourName, String cruiseName,
                              LocalDate startDate, LocalDate endDate, List<RoomDetails> rooms,
                              List<PassengerItineraryDayResponse> itinerary,
                              List<com.project.tour.service.passenger.PassengerTripActivities.Activity> activities,
                              List<com.project.tour.service.passenger.PassengerTripCatalog.Item> catalog) {}
}
