package com.project.tour.service;

import com.project.tour.dto.cruisedeck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruisedeck.CruiseDeckResponse;
import com.project.tour.dto.cruisedeck.UpdateCruiseDeckRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.CruiseDeckStatus;
import com.project.tour.repository.CruiseDeckRepository;
import com.project.tour.repository.CruiseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseDeckService {

    private final CruiseDeckRepository cruiseDeckRepository;
    private final CruiseRepository cruiseRepository;

    public CruiseDeckService(
        CruiseDeckRepository cruiseDeckRepository,
        CruiseRepository cruiseRepository
    ) {
        this.cruiseDeckRepository = cruiseDeckRepository;
        this.cruiseRepository = cruiseRepository;
    }

    public CruiseDeckResponse createCruiseDeck(
        UUID cruiseId,
        CreateCruiseDeckRequest request
    ) {
        Cruise cruise = findCruiseById(cruiseId);

        validateDeckNumber(cruise, request.deckNumber());

        if (cruiseDeckRepository
            .existsByCruise_IdAndDeckNumber(
                cruiseId,
                request.deckNumber()
            )) {

            throw new DuplicateResourceException(
                "Deck number " + request.deckNumber()
                    + " already exists in cruise: " + cruiseId
            );
        }

        CruiseDeck cruiseDeck = new CruiseDeck();

        cruiseDeck.setCruise(cruise);
        cruiseDeck.setDeckNumber(request.deckNumber());
        cruiseDeck.setStatus(CruiseDeckStatus.ACTIVE);

        CruiseDeck savedDeck =
            cruiseDeckRepository.save(cruiseDeck);

        return toResponse(savedDeck);
    }

    @Transactional(readOnly = true)
    public CruiseDeckResponse getCruiseDeckById(
        UUID cruiseId,
        UUID deckId
    ) {
        CruiseDeck cruiseDeck =
            findCruiseDeck(cruiseId, deckId);

        return toResponse(cruiseDeck);
    }

    @Transactional(readOnly = true)
    public List<CruiseDeckResponse> getCruiseDecks(
        UUID cruiseId
    ) {
        ensureCruiseExists(cruiseId);

        return cruiseDeckRepository
            .findAllByCruise_IdOrderByDeckNumberAsc(cruiseId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CruiseDeckResponse> getActiveCruiseDecks(
        UUID cruiseId
    ) {
        ensureCruiseExists(cruiseId);

        return cruiseDeckRepository
            .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                cruiseId,
                CruiseDeckStatus.ACTIVE
            )
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public CruiseDeckResponse updateCruiseDeck(
        UUID cruiseId,
        UUID deckId,
        UpdateCruiseDeckRequest request
    ) {
        CruiseDeck cruiseDeck =
            findCruiseDeck(cruiseId, deckId);

        Cruise cruise = cruiseDeck.getCruise();

        validateDeckNumber(cruise, request.deckNumber());

        boolean duplicateDeckNumber = cruiseDeckRepository
            .existsByCruise_IdAndDeckNumberAndIdNot(
                cruiseId,
                request.deckNumber(),
                deckId
            );

        if (duplicateDeckNumber) {
            throw new DuplicateResourceException(
                "Deck number " + request.deckNumber()
                    + " already exists in cruise: " + cruiseId
            );
        }

        cruiseDeck.setDeckNumber(request.deckNumber());
        cruiseDeck.setStatus(request.status());

        CruiseDeck updatedDeck =
            cruiseDeckRepository.save(cruiseDeck);

        return toResponse(updatedDeck);
    }

    public CruiseDeckResponse deactivateCruiseDeck(
        UUID cruiseId,
        UUID deckId
    ) {
        CruiseDeck cruiseDeck =
            findCruiseDeck(cruiseId, deckId);

        cruiseDeck.setStatus(CruiseDeckStatus.INACTIVE);

        CruiseDeck updatedDeck =
            cruiseDeckRepository.save(cruiseDeck);

        return toResponse(updatedDeck);
    }

    private Cruise findCruiseById(UUID cruiseId) {
        return cruiseRepository
            .findById(cruiseId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise not found with id: " + cruiseId
            ));
    }

    private void ensureCruiseExists(UUID cruiseId) {
        if (!cruiseRepository.existsById(cruiseId)) {
            throw new ResourceNotFoundException(
                "Cruise not found with id: " + cruiseId
            );
        }
    }

    private CruiseDeck findCruiseDeck(
        UUID cruiseId,
        UUID deckId
    ) {
        return cruiseDeckRepository
            .findByIdAndCruise_Id(deckId, cruiseId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise deck not found with id: "
                    + deckId
                    + " in cruise: "
                    + cruiseId
            ));
    }

    private void validateDeckNumber(
        Cruise cruise,
        Integer deckNumber
    ) {
        if (deckNumber > cruise.getTotalDecks()) {
            throw new IllegalArgumentException(
                "Deck number must not exceed cruise total decks: "
                    + cruise.getTotalDecks()
            );
        }
    }

    private CruiseDeckResponse toResponse(
        CruiseDeck cruiseDeck
    ) {
        return new CruiseDeckResponse(
            cruiseDeck.getId(),
            cruiseDeck.getCruise().getId(),
            cruiseDeck.getDeckNumber(),
            cruiseDeck.getStatus()
        );
    }
}