package com.project.tour.service;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.CruiseDeckRepository;
import com.project.tour.repository.RoomRepository;
import com.project.tour.repository.RoomTypeRepository;
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
class RoomServiceTests {

    @Mock RoomRepository roomRepository;
    @Mock CruiseDeckRepository deckRepository;
    @Mock RoomTypeRepository roomTypeRepository;
    RoomService service;

    @BeforeEach
    void setUp() {
        service = new RoomService(roomRepository, deckRepository, roomTypeRepository);
    }

    @Test
    void createRoomNormalizesCodeAndLinksReferences() {
        UUID deckId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        CruiseDeck deck = new CruiseDeck();
        deck.setId(deckId);
        RoomType type = new RoomType();
        type.setId(typeId);
        type.setName("Suite");

        when(deckRepository.findById(deckId)).thenReturn(Optional.of(deck));
        when(roomTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(roomRepository.existsByCruiseDeck_IdAndCodeIgnoreCase(deckId, "A-101"))
            .thenReturn(false);
        when(roomRepository.save(any(Room.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        RoomResponse response = service.createRoom(
            deckId,
            new CreateRoomRequest(" a-101 ", typeId)
        );

        assertEquals("A-101", response.code());
        assertEquals(typeId, response.roomTypeId());
        assertEquals(RoomStatus.ACTIVE, response.status());
    }

    @Test
    void createRoomRejectsDuplicateCodeInDeck() {
        UUID deckId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(new CruiseDeck()));
        when(roomTypeRepository.findById(typeId)).thenReturn(Optional.of(new RoomType()));
        when(roomRepository.existsByCruiseDeck_IdAndCodeIgnoreCase(deckId, "A-101"))
            .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createRoom(
            deckId,
            new CreateRoomRequest("A-101", typeId)
        ));
    }

    @Test
    void createRoomRejectsMissingDeck() {
        UUID deckId = UUID.randomUUID();
        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createRoom(
            deckId,
            new CreateRoomRequest("A-101", UUID.randomUUID())
        ));
    }
}
