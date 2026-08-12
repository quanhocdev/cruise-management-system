package com.project.tour.mapper.cruise;

import com.project.tour.dto.cruise.deck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;

public class CruiseDeckMapper {

    private CruiseDeckMapper() {
    }

    /*
     * =====================================================
     * CREATE REQUEST -> ENTITY
     * =====================================================
     */
    public static CruiseDeck toEntity(
            CreateCruiseDeckRequest request,
            Cruise cruise) {

        if (request == null) {
            return null;
        }

        CruiseDeck deck = new CruiseDeck();

        deck.setCruise(cruise);
        deck.setDeckNumber(request.getDeckNumber());

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