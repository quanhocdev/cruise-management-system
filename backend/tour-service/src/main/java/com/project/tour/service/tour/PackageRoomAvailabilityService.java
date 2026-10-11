package com.project.tour.service.tour;

import com.project.tour.model.enums.RoomStatus;
import com.project.tour.repository.room.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PackageRoomAvailabilityService {

    private final RoomRepository roomRepository;

    public PackageRoomAvailabilityService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /** Đếm số phòng ACTIVE của một hạng phòng trên cruise. */
    public int countActiveRooms(UUID cruiseId, UUID roomTypeId) {
        return (int) roomRepository.countByCruiseDeck_CruiseIdAndRoomTypeIdAndStatus(
                cruiseId,
                roomTypeId,
                RoomStatus.ACTIVE);
    }
}