package com.project.tour.controller;

import com.project.tour.dto.cruisedeck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruisedeck.CruiseDeckResponse;
import com.project.tour.dto.cruisedeck.UpdateCruiseDeckRequest;
import com.project.tour.service.CruiseDeckService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cruises/{cruiseId}/decks")
public class CruiseDeckController {

    private final CruiseDeckService cruiseDeckService;

    public CruiseDeckController(
        CruiseDeckService cruiseDeckService
    ) {
        this.cruiseDeckService = cruiseDeckService;
    }

    @PostMapping
    public ResponseEntity<CruiseDeckResponse> createCruiseDeck(
        @PathVariable UUID cruiseId,
        @Valid @RequestBody CreateCruiseDeckRequest request
    ) {
        CruiseDeckResponse response =
            cruiseDeckService.createCruiseDeck(
                cruiseId,
                request
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CruiseDeckResponse>> getCruiseDecks(
        @PathVariable UUID cruiseId,
        @RequestParam(defaultValue = "false")
        boolean activeOnly
    ) {
        List<CruiseDeckResponse> response;

        if (activeOnly) {
            response =
                cruiseDeckService.getActiveCruiseDecks(cruiseId);
        } else {
            response =
                cruiseDeckService.getCruiseDecks(cruiseId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> getCruiseDeckById(
        @PathVariable UUID cruiseId,
        @PathVariable UUID deckId
    ) {
        CruiseDeckResponse response =
            cruiseDeckService.getCruiseDeckById(
                cruiseId,
                deckId
            );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> updateCruiseDeck(
        @PathVariable UUID cruiseId,
        @PathVariable UUID deckId,
        @Valid @RequestBody UpdateCruiseDeckRequest request
    ) {
        CruiseDeckResponse response =
            cruiseDeckService.updateCruiseDeck(
                cruiseId,
                deckId,
                request
            );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{deckId}/deactivate")
    public ResponseEntity<CruiseDeckResponse> deactivateCruiseDeck(
        @PathVariable UUID cruiseId,
        @PathVariable UUID deckId
    ) {
        CruiseDeckResponse response =
            cruiseDeckService.deactivateCruiseDeck(
                cruiseId,
                deckId
            );

        return ResponseEntity.ok(response);
    }
}