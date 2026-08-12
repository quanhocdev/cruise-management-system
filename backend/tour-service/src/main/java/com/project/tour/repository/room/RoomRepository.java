package com.project.tour.repository.room;

import com.project.tour.model.Room;
import com.project.tour.model.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

        boolean existsByCruiseArea_IdAndCodeIgnoreCase(
                        UUID areaId,
                        String code);

        boolean existsByCruiseArea_IdAndCodeIgnoreCaseAndIdNot(
                        UUID areaId,
                        String code,
                        UUID excludedRoomId);

        Optional<Room> findByIdAndCruiseArea_Id(
                        UUID id,
                        UUID areaId);

        List<Room> findAllByCruiseArea_IdOrderByCodeAsc(
                        UUID areaId);

        List<Room> findAllByCruiseArea_IdAndStatusOrderByCodeAsc(
                        UUID areaId,
                        RoomStatus status);

        boolean existsByRoomType_Id(UUID roomTypeId);
}