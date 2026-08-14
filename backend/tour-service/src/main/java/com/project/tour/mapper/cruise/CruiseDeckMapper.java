package com.project.tour.mapper.cruise;

import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;

public class CruiseDeckMapper {

    private CruiseDeckMapper() {
    }

    /*
     * =====================================================
     * CREATE ENTITY
     * =====================================================
     *
     * Dùng khi Service tự sinh từng tầng.
     *
     * Ví dụ:
     * totalDecks = 5
     *
     * Service sẽ gọi:
     *
     * toEntity(cruise, 1)
     * toEntity(cruise, 2)
     * toEntity(cruise, 3)
     * toEntity(cruise, 4)
     * toEntity(cruise, 5)
     *
     */
    public static CruiseDeck toEntity(
            Cruise cruise,
            Integer deckNumber) {

        if (cruise == null || deckNumber == null) {
            return null;
        }

        CruiseDeck deck = new CruiseDeck();

        deck.setCruise(cruise);
        deck.setDeckNumber(deckNumber);

        return deck;
    }

    /*
     * =====================================================
     * UPDATE REQUEST -> ENTITY
     * =====================================================
     */
    public static void updateEntity(
            CruiseDeck deck,
            UpdateCruiseDeckRequest request) {

        if (deck == null || request == null) {
            return;
        }

        deck.setDeckNumber(request.getDeckNumber());
        deck.setStatus(request.getStatus());
    }

    /*
     * =====================================================
     * ENTITY -> RESPONSE
     * =====================================================
     */
    public static CruiseDeckResponse toResponse(
            CruiseDeck deck) {

        if (deck == null) {
            return null;
        }

        return new CruiseDeckResponse(
                deck.getId(),
                deck.getCruise().getId(),
                deck.getCruise().getName(),
                deck.getDeckNumber(),
                deck.getStatus());
    }
}