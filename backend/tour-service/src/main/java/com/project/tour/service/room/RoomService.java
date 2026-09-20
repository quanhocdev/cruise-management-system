package com.project.tour.service.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.room.RoomMapper;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.TourPackage;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.room.RoomTypeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.repository.tour.TourPackageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomService {

        private final RoomRepository roomRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final RoomTypeRepository roomTypeRepository;
        private final TourPackageRepository tourPackageRepository;

        public RoomService(
                        RoomRepository roomRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        RoomTypeRepository roomTypeRepository,
                        TourPackageRepository tourPackageRepository) {

                this.roomRepository = roomRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.roomTypeRepository = roomTypeRepository;
                this.tourPackageRepository = tourPackageRepository;
        }

        // =====================================================
        // CREATE ROOMS
        // =====================================================

        public List<RoomResponse> createRooms(
                        UUID deckId,
                        CreateRoomRequest request) {

                CruiseDeck deck = findDeck(deckId);

                RoomType roomType = findRoomType(
                                request.roomTypeId());

                int startNumber = getNextRoomNumber(deck);

                List<Room> rooms = new ArrayList<>();

                for (int i = 0; i < request.quantity(); i++) {

                        String code = String.valueOf(
                                        startNumber + i);

                        if (roomRepository
                                        .existsByCruiseDeck_IdAndCodeIgnoreCase(
                                                        deckId,
                                                        code)) {

                                throw new AppException(
                                                "Room code already exists: " + code,
                                                HttpStatus.CONFLICT);
                        }

                        rooms.add(
                                        RoomMapper.toEntity(
                                                        deck,
                                                        roomType,
                                                        code));
                }

                return roomRepository
                                .saveAll(rooms)
                                .stream()
                                .map(RoomMapper::toResponse)
                                .toList();
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
        // GET ALL ROOMS
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
        // GET ACTIVE ROOMS
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

                return RoomMapper.toResponse(
                                roomRepository.save(room));
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
        // GET NEXT ROOM NUMBER
        // =====================================================

        private int getNextRoomNumber(
                        CruiseDeck deck) {

                List<Room> rooms = roomRepository
                                .findAllByCruiseDeck_IdOrderByCodeAsc(
                                                deck.getId());

                int max = 0;

                for (Room room : rooms) {

                        try {

                                int number = Integer.parseInt(room.getCode());

                                max = Math.max(max, number);

                        } catch (NumberFormatException ignored) {
                        }
                }

                return max + 1;
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

        // =====================================================
        // GET AVAILABLE ROOMS FOR BOOKING (Dùng tourPackageId)
        // =====================================================

        @Transactional(readOnly = true)
        public List<RoomResponse> getAvailableRoomsForBooking(
                        Long bookingId,
                        UUID tourPackageId) { // Đổi tham số từ roomTypeId thành tourPackageId

                System.out.println("[SERVICE] Lọc phòng trống cho bookingId: " + bookingId + " với tourPackageId: "
                                + tourPackageId);

                // 1. Từ tourPackageId, tour-service tự tra cứu trong bảng tour_packages để lấy
                // roomTypeId
                TourPackage tourPackage = tourPackageRepository.findById(tourPackageId)
                                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));

                UUID roomTypeId = tourPackage.getRoomTypeId();

                // 2. Lấy danh sách phòng trống dựa theo roomTypeId đã tìm được
                List<Room> rooms = roomRepository.findAllByRoomType_IdAndStatus(roomTypeId, RoomStatus.ACTIVE);

                return rooms.stream()
                                .map(RoomMapper::toResponse)
                                .toList();
        }
}