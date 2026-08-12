package com.project.tour.controller.cruise;

import com.project.tour.dto.cruise.deck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.service.cruise.CruiseDeckService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cruises/{cruiseId}/decks")
public class CruiseDeckController {

    private final CruiseDeckService cruiseDeckService;

    public CruiseDeckController(
            CruiseDeckService cruiseDeckService) {

        this.cruiseDeckService = cruiseDeckService;
    }

    @PostMapping
    public ResponseEntity<CruiseDeckResponse> createDeck(
            @PathVariable UUID cruiseId,
            @Valid @RequestBody CreateCruiseDeckRequest request) {

        request.setCruiseId(cruiseId);

        CruiseDeckResponse response = cruiseDeckService.createDeck(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CruiseDeckResponse>> getDecks(
            @PathVariable UUID cruiseId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<CruiseDeckResponse> response = activeOnly
                ? cruiseDeckService.getActiveDecksByCruise(cruiseId)
                : cruiseDeckService.getDecksByCruise(cruiseId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> getDeckById(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId) {

        return ResponseEntity.ok(
                cruiseDeckService.getDeckById(deckId));
    }

    @PatchMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> updateDeck(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId,
            @Valid @RequestBody UpdateCruiseDeckRequest request) {

        return ResponseEntity.ok(
                cruiseDeckService.updateDeck(
                        deckId,
                        request));
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId) {

        cruiseDeckService.deleteDeck(deckId);

        return ResponseEntity.noContent().build();
    }
}