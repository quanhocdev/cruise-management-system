
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.cruise.PassengerCruiseDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseDeckDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseAreaDTO;
import com.project.tour.service.passenger.PassengerCruiseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerCruiseController {

    private final PassengerCruiseService service;

    public PassengerCruiseController(PassengerCruiseService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/cruise")
    public ResponseEntity<PassengerCruiseDTO> getCruise(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getCruise(tourId));
    }

    @GetMapping("/{tourId}/cruise/decks")
    public ResponseEntity<List<PassengerCruiseDeckDTO>> getDecks(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getDecks(tourId));
    }

    @GetMapping("/{tourId}/cruise/decks/{deckId}/areas")
    public ResponseEntity<List<PassengerCruiseAreaDTO>> getAreas(
            @PathVariable UUID tourId,
            @PathVariable UUID deckId) {
        return ResponseEntity.ok(service.getAreas(tourId, deckId));
    }
}
