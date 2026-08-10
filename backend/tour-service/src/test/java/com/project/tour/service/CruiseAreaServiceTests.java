package com.project.tour.service;

import com.project.tour.dto.cruisearea.CreateCruiseAreaRequest;
import com.project.tour.dto.cruisearea.CruiseAreaResponse;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.CruiseAreaStatus;
import com.project.tour.repository.CruiseAreaRepository;
import com.project.tour.repository.CruiseDeckRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CruiseAreaServiceTests {

    @Mock CruiseAreaRepository areaRepository;
    @Mock CruiseDeckRepository deckRepository;
    CruiseAreaService service;

    @BeforeEach
    void setUp() {
        service = new CruiseAreaService(areaRepository, deckRepository);
    }

    @Test
    void createAreaTrimsDataAndLinksDeck() {
        UUID deckId = UUID.randomUUID();
        CruiseDeck deck = new CruiseDeck();
        deck.setId(deckId);

        when(deckRepository.findById(deckId)).thenReturn(Optional.of(deck));
        when(areaRepository.existsByCruiseDeck_IdAndNameIgnoreCase(deckId, "Restaurant"))
            .thenReturn(false);
        when(areaRepository.save(any(CruiseArea.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CruiseAreaResponse response = service.createArea(
            deckId,
            new CreateCruiseAreaRequest(" Restaurant ", " Main dining area ")
        );

        assertEquals("Restaurant", response.name());
        assertEquals("Main dining area", response.description());
        assertEquals(CruiseAreaStatus.ACTIVE, response.status());
        assertEquals(deckId, response.cruiseDeckId());
    }

    @Test
    void createAreaRejectsDuplicateNameInDeck() {
        UUID deckId = UUID.randomUUID();
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(new CruiseDeck()));
        when(areaRepository.existsByCruiseDeck_IdAndNameIgnoreCase(deckId, "Restaurant"))
            .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createArea(
            deckId,
            new CreateCruiseAreaRequest("Restaurant", null)
        ));
    }

    @Test
    void createAreaRejectsMissingDeck() {
        UUID deckId = UUID.randomUUID();
        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createArea(
            deckId,
            new CreateCruiseAreaRequest("Restaurant", null)
        ));
    }
}
