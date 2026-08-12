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

    /*
     * =====================================================
     * CREATE
     * POST /api/admin/cruises/{cruiseId}/decks
     * =====================================================
     */
    @PostMapping
    public ResponseEntity<CruiseDeckResponse> createDeck(
            @PathVariable UUID cruiseId,
            @Valid @RequestBody CreateCruiseDeckRequest request) {

        /*
         * cruiseId trên URL là nguồn chính.
         * Không tin cruiseId trong body.
         */
        request.setCruiseId(cruiseId);

        CruiseDeckResponse response = cruiseDeckService.createDeck(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * =====================================================
     * GET ALL
     * GET /api/admin/cruises/{cruiseId}/decks
     * =====================================================
     */
    @GetMapping
    public ResponseEntity<List<CruiseDeckResponse>> getDecks(
            @PathVariable UUID cruiseId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<CruiseDeckResponse> response;

        if (activeOnly) {

            response = cruiseDeckService
                    .getActiveDecksByCruise(cruiseId);

        } else {

            response = cruiseDeckService
                    .getDecksByCruise(cruiseId);
        }

        return ResponseEntity.ok(response);
    }

    /*
     * =====================================================
     * GET BY ID
     * GET /api/admin/cruises/{cruiseId}/decks/{deckId}
     * =====================================================
     */
    @GetMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> getDeckById(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId) {

        CruiseDeckResponse response = cruiseDeckService.getDeckById(deckId);

        /*
         * Có thể kiểm tra deck thuộc cruise ở tầng service
         * nếu cần strict validation.
         */

        return ResponseEntity.ok(response);
    }

    /*
     * =====================================================
     * UPDATE
     * PUT /api/admin/cruises/{cruiseId}/decks/{deckId}
     * =====================================================
     */
    @PutMapping("/{deckId}")
    public ResponseEntity<CruiseDeckResponse> updateDeck(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId,
            @Valid @RequestBody UpdateCruiseDeckRequest request) {

        CruiseDeckResponse response = cruiseDeckService.updateDeck(
                deckId,
                request);

        return ResponseEntity.ok(response);
    }

    /*
     * =====================================================
     * DEACTIVATE
     * PATCH /api/admin/cruises/{cruiseId}/decks/{deckId}/deactivate
     * =====================================================
     */
    @PatchMapping("/{deckId}/deactivate")
    public ResponseEntity<CruiseDeckResponse> deactivateDeck(
            @PathVariable UUID cruiseId,
            @PathVariable UUID deckId) {

        CruiseDeckResponse response = cruiseDeckService.deactivateDeck(deckId);

        return ResponseEntity.ok(response);
    }
}