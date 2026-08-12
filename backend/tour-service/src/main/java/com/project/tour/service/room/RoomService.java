package com.project.tour.service.room;

import com.project.tour.dto.room.CreateRoomRequest;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.room.UpdateRoomRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.room.RoomMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Room;
import com.project.tour.model.RoomType;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
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
    private final CruiseAreaRepository cruiseAreaRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(
            RoomRepository roomRepository,
            CruiseAreaRepository cruiseAreaRepository,
            RoomTypeRepository roomTypeRepository) {

        this.roomRepository = roomRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomResponse createRoom(
            UUID areaId,
            CreateRoomRequest request) {

        CruiseArea area = findArea(areaId);

        if (roomRepository.existsByCruiseArea_IdAndCodeIgnoreCase(
                areaId,
                request.getCode())) {

            throw new AppException(
                    "Room code already exists in this area",
                    HttpStatus.CONFLICT);
        }

        RoomType roomType = findRoomType(
                request.getRoomTypeId());

        Room room = RoomMapper.toEntity(
                request,
                roomType);

        room.setCruiseArea(area);

        Room savedRoom = roomRepository.save(room);

        return RoomMapper.toResponse(savedRoom);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(
            UUID areaId,
            UUID roomId) {

        Room room = findById(areaId, roomId);

        return RoomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByArea(
            UUID areaId) {

        findArea(areaId);

        return roomRepository
                .findAllByCruiseArea_IdOrderByCodeAsc(areaId)
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getActiveRoomsByArea(
            UUID areaId) {

        findArea(areaId);

        return roomRepository
                .findAllByCruiseArea_IdAndStatusOrderByCodeAsc(
                        areaId,
                        RoomStatus.ACTIVE)
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    public RoomResponse updateRoom(
            UUID areaId,
            UUID roomId,
            UpdateRoomRequest request) {

        Room room = findById(areaId, roomId);

        if (roomRepository
                .existsByCruiseArea_IdAndCodeIgnoreCaseAndIdNot(
                        areaId,
                        request.getCode(),
                        roomId)) {

            throw new AppException(
                    "Room code already exists in this area",
                    HttpStatus.CONFLICT);
        }

        RoomType roomType = findRoomType(
                request.getRoomTypeId());

        RoomMapper.updateEntity(
                room,
                request,
                roomType);

        Room updatedRoom = roomRepository.save(room);

        return RoomMapper.toResponse(updatedRoom);
    }

    public void deleteRoom(
            UUID areaId,
            UUID roomId) {

        Room room = findById(areaId, roomId);

        roomRepository.delete(room);
    }

    private CruiseArea findArea(UUID areaId) {

        return cruiseAreaRepository
                .findById(areaId)
                .orElseThrow(() -> new AppException(
                        "Cruise area not found",
                        HttpStatus.NOT_FOUND));
    }

    private RoomType findRoomType(UUID roomTypeId) {

        return roomTypeRepository
                .findById(roomTypeId)
                .orElseThrow(() -> new AppException(
                        "Room type not found",
                        HttpStatus.NOT_FOUND));
    }

    private Room findById(
            UUID areaId,
            UUID roomId) {

        return roomRepository
                .findByIdAndCruiseArea_Id(
                        roomId,
                        areaId)
                .orElseThrow(() -> new AppException(
                        "Room not found",
                        HttpStatus.NOT_FOUND));
    }
}