package com.project.tour.service;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.CruiseDeckRepository;
import com.project.tour.repository.RoomRepository;
import com.project.tour.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final CruiseDeckRepository cruiseDeckRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(
        RoomRepository roomRepository,
        CruiseDeckRepository cruiseDeckRepository,
        RoomTypeRepository roomTypeRepository
    ) {
        this.roomRepository = roomRepository;
        this.cruiseDeckRepository = cruiseDeckRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomResponse createRoom(UUID deckId, CreateRoomRequest request) {
        CruiseDeck deck = findDeck(deckId);
        RoomType roomType = findRoomType(request.roomTypeId());
        String code = normalizeCode(request.code());

        if (roomRepository.existsByCruiseDeck_IdAndCodeIgnoreCase(deckId, code)) {
            throw new DuplicateResourceException(
                "Room code already exists in deck: " + code
            );
        }

        Room room = new Room();
        room.setCruiseDeck(deck);
        room.setCode(code);
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.ACTIVE);

        return toResponse(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(UUID deckId, UUID roomId) {
        return toResponse(findRoom(deckId, roomId));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRooms(UUID deckId, boolean activeOnly) {
        ensureDeckExists(deckId);

        List<Room> rooms = activeOnly
            ? roomRepository.findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(
                deckId,
                RoomStatus.ACTIVE
            )
            : roomRepository.findAllByCruiseDeck_IdOrderByCodeAsc(deckId);

        return rooms.stream().map(this::toResponse).toList();
    }

    public RoomResponse updateRoom(
        UUID deckId,
        UUID roomId,
        UpdateRoomRequest request
    ) {
        Room room = findRoom(deckId, roomId);
        RoomType roomType = findRoomType(request.roomTypeId());
        String code = normalizeCode(request.code());

        if (roomRepository.existsByCruiseDeck_IdAndCodeIgnoreCaseAndIdNot(
            deckId,
            code,
            roomId
        )) {
            throw new DuplicateResourceException(
                "Room code already exists in deck: " + code
            );
        }

        room.setCode(code);
        room.setRoomType(roomType);
        room.setStatus(request.status());

        return toResponse(roomRepository.save(room));
    }

    public RoomResponse deactivateRoom(UUID deckId, UUID roomId) {
        Room room = findRoom(deckId, roomId);
        room.setStatus(RoomStatus.INACTIVE);
        return toResponse(roomRepository.save(room));
    }

    private CruiseDeck findDeck(UUID deckId) {
        return cruiseDeckRepository.findById(deckId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise deck not found with id: " + deckId
            ));
    }

    private void ensureDeckExists(UUID deckId) {
        if (!cruiseDeckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException(
                "Cruise deck not found with id: " + deckId
            );
        }
    }

    private RoomType findRoomType(UUID roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Room type not found with id: " + roomTypeId
            ));
    }

    private Room findRoom(UUID deckId, UUID roomId) {
        return roomRepository.findByIdAndCruiseDeck_Id(roomId, deckId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Room not found with id: " + roomId + " in deck: " + deckId
            ));
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
            room.getId(),
            room.getCruiseDeck().getId(),
            room.getCode(),
            room.getRoomType().getId(),
            room.getRoomType().getName(),
            room.getStatus()
        );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
