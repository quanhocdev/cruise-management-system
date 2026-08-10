package com.project.tour.service;

import com.project.tour.dto.roomtype.CreateRoomTypeRequest;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.roomtype.UpdateRoomTypeRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.RoomType;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceTests {

    @Mock
    private RoomTypeRepository repository;

    private RoomTypeService service;

    @BeforeEach
    void setUp() {
        service = new RoomTypeService(repository);
    }

    @Test
    void createRoomTypeTrimsValues() {
        when(repository.existsByNameIgnoreCase("Deluxe Room"))
            .thenReturn(false);
        when(repository.save(any(RoomType.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        RoomTypeResponse response = service.createRoomType(
            new CreateRoomTypeRequest(" Deluxe Room ", " Ocean view ")
        );

        assertEquals("Deluxe Room", response.name());
        assertEquals("Ocean view", response.description());
    }

    @Test
    void createRoomTypeRejectsDuplicateName() {
        when(repository.existsByNameIgnoreCase("Suite"))
            .thenReturn(true);

        assertThrows(
            DuplicateResourceException.class,
            () -> service.createRoomType(
                new CreateRoomTypeRequest("Suite", null)
            )
        );
        verify(repository, never()).save(any(RoomType.class));
    }

    @Test
    void updateRoomTypeExcludesCurrentRecordFromDuplicateCheck() {
        UUID id = UUID.randomUUID();
        RoomType roomType = new RoomType();
        roomType.setId(id);
        roomType.setName("Suite");

        when(repository.findById(id)).thenReturn(Optional.of(roomType));
        when(repository.existsByNameIgnoreCaseAndIdNot("Suite", id))
            .thenReturn(false);
        when(repository.save(roomType)).thenReturn(roomType);

        RoomTypeResponse response = service.updateRoomType(
            id,
            new UpdateRoomTypeRequest("Suite", "Updated")
        );

        assertEquals("Updated", response.description());
    }

    @Test
    void getRoomTypeRejectsMissingId() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> service.getRoomTypeById(id)
        );
    }
}
