package com.project.tour.service.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.room.RoomMapper;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.room.RoomTypeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                        RoomTypeRepository roomTypeRepository) {

                this.roomRepository = roomRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.roomTypeRepository = roomTypeRepository;
        }

        // =====================================================
        // CREATE ROOM
        // =====================================================

        public RoomResponse createRoom(
                        UUID deckId,
                        CreateRoomRequest request) {

                CruiseDeck deck = findDeck(deckId);

                if (roomRepository.existsByCruiseDeck_IdAndCodeIgnoreCase(
                                deckId,
                                request.code())) {

                        throw new AppException(
                                        "Room code already exists in this deck",
                                        HttpStatus.CONFLICT);
                }

                RoomType roomType = findRoomType(
                                request.roomTypeId());

                Room room = RoomMapper.toEntity(
                                request,
                                deck,
                                roomType);

                Room savedRoom = roomRepository.save(room);

                return RoomMapper.toResponse(savedRoom);
        }

        // =====================================================
        // GET ROOM BY ID
        // =====================================================

        @Transactional(readOnly = true)
        public RoomResponse getRoomById(
                        UUID deckId,
                        UUID roomId) {

                Room room = findById(
                                deckId,
                                roomId);

                return RoomMapper.toResponse(room);
        }

        // =====================================================
        // GET ALL ROOMS BY DECK
        // =====================================================

        @Transactional(readOnly = true)
        public List<RoomResponse> getRoomsByDeck(
                        UUID deckId) {

                findDeck(deckId);

                return roomRepository
                                .findAllByCruiseDeck_IdOrderByCodeAsc(deckId)
                                .stream()
                                .map(RoomMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET ACTIVE ROOMS BY DECK
        // =====================================================

        @Transactional(readOnly = true)
        public List<RoomResponse> getActiveRoomsByDeck(
                        UUID deckId) {

                findDeck(deckId);

                return roomRepository
                                .findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(
                                                deckId,
                                                RoomStatus.ACTIVE)
                                .stream()
                                .map(RoomMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // UPDATE ROOM
        // =====================================================

        public RoomResponse updateRoom(
                        UUID deckId,
                        UUID roomId,
                        UpdateRoomRequest request) {

                Room room = findById(
                                deckId,
                                roomId);

                if (roomRepository
                                .existsByCruiseDeck_IdAndCodeIgnoreCaseAndIdNot(
                                                deckId,
                                                request.code(),
                                                roomId)) {

                        throw new AppException(
                                        "Room code already exists in this deck",
                                        HttpStatus.CONFLICT);
                }

                RoomType roomType = findRoomType(
                                request.roomTypeId());

                RoomMapper.updateEntity(
                                room,
                                request,
                                roomType);

                Room updatedRoom = roomRepository.save(room);

                return RoomMapper.toResponse(updatedRoom);
        }

        // =====================================================
        // DELETE ROOM
        // =====================================================

        public void deleteRoom(
                        UUID deckId,
                        UUID roomId) {

                Room room = findById(
                                deckId,
                                roomId);

                roomRepository.delete(room);
        }

        // =====================================================
        // FIND DECK
        // =====================================================

        private CruiseDeck findDeck(
                        UUID deckId) {

                return cruiseDeckRepository
                                .findById(deckId)
                                .orElseThrow(() -> new AppException(
                                                "Cruise deck not found",
                                                HttpStatus.NOT_FOUND));
        }

        // =====================================================
        // FIND ROOM TYPE
        // =====================================================

        private RoomType findRoomType(
                        UUID roomTypeId) {

                return roomTypeRepository
                                .findById(roomTypeId)
                                .orElseThrow(() -> new AppException(
                                                "Room type not found",
                                                HttpStatus.NOT_FOUND));
        }

        // =====================================================
        // FIND ROOM
        // =====================================================

        private Room findById(
                        UUID deckId,
                        UUID roomId) {

                return roomRepository
                                .findByIdAndCruiseDeck_Id(
                                                roomId,
                                                deckId)
                                .orElseThrow(() -> new AppException(
                                                "Room not found",
                                                HttpStatus.NOT_FOUND));
        }
}
