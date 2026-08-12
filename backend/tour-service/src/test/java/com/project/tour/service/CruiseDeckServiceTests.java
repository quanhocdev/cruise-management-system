package com.project.tour.service;

import com.project.tour.dto.cruise.deck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.CruiseDeckStatus;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.cruise.CruiseRepository;
import com.project.tour.service.cruise.CruiseDeckService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CruiseDeckServiceTests {

        @Mock
        private CruiseDeckRepository cruiseDeckRepository;

        @Mock
        private CruiseRepository cruiseRepository;

        private CruiseDeckService cruiseDeckService;

        private UUID cruiseId;
        private Cruise cruise;

        @BeforeEach
        void setUp() {
                cruiseDeckService = new CruiseDeckService(
                                cruiseDeckRepository,
                                cruiseRepository);

                cruiseId = UUID.randomUUID();
                cruise = new Cruise();
                cruise.setId(cruiseId);
                cruise.setTotalDecks(12);
        }

        @Test
        void createDeckForExistingCruise() {
                when(cruiseRepository.findById(cruiseId))
                                .thenReturn(Optional.of(cruise));
                when(cruiseDeckRepository
                                .existsByCruise_IdAndDeckNumber(cruiseId, 3))
                                .thenReturn(false);
                when(cruiseDeckRepository.save(any(CruiseDeck.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                CruiseDeckResponse response = cruiseDeckService
                                .createCruiseDeck(
                                                cruiseId,
                                                new CreateCruiseDeckRequest(3));

                assertEquals(cruiseId, response.cruiseId());
                assertEquals(3, response.deckNumber());
                assertEquals(CruiseDeckStatus.ACTIVE, response.status());
        }

        @Test
        void createDeckRejectsNumberAboveCruiseTotalDecks() {
                when(cruiseRepository.findById(cruiseId))
                                .thenReturn(Optional.of(cruise));

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> cruiseDeckService.createCruiseDeck(
                                                cruiseId,
                                                new CreateCruiseDeckRequest(13)));

                assertEquals(
                                "Deck number must not exceed cruise total decks: 12",
                                exception.getMessage());
                verify(cruiseDeckRepository, never())
                                .save(any(CruiseDeck.class));
        }

        @Test
        void createDeckRejectsDuplicateNumberInSameCruise() {
                when(cruiseRepository.findById(cruiseId))
                                .thenReturn(Optional.of(cruise));
                when(cruiseDeckRepository
                                .existsByCruise_IdAndDeckNumber(cruiseId, 3))
                                .thenReturn(true);

                assertThrows(
                                DuplicateResourceException.class,
                                () -> cruiseDeckService.createCruiseDeck(
                                                cruiseId,
                                                new CreateCruiseDeckRequest(3)));
        }

        @Test
        void createDeckRejectsMissingCruise() {
                when(cruiseRepository.findById(cruiseId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ResourceNotFoundException.class,
                                () -> cruiseDeckService.createCruiseDeck(
                                                cruiseId,
                                                new CreateCruiseDeckRequest(1)));
        }

        @Test
        void updateDeckRejectsNumberUsedByAnotherDeck() {
                UUID deckId = UUID.randomUUID();
                CruiseDeck deck = new CruiseDeck();
                deck.setId(deckId);
                deck.setCruise(cruise);
                deck.setDeckNumber(2);

                when(cruiseDeckRepository.findByIdAndCruise_Id(deckId, cruiseId))
                                .thenReturn(Optional.of(deck));
                when(cruiseDeckRepository
                                .existsByCruise_IdAndDeckNumberAndIdNot(
                                                cruiseId,
                                                3,
                                                deckId))
                                .thenReturn(true);

                assertThrows(
                                DuplicateResourceException.class,
                                () -> cruiseDeckService.updateCruiseDeck(
                                                cruiseId,
                                                deckId,
                                                new UpdateCruiseDeckRequest(
                                                                3,
                                                                CruiseDeckStatus.ACTIVE)));
        }
}
