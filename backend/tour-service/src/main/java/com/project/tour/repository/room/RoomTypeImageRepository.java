package com.project.tour.repository.room;

import com.project.tour.model.admin.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, UUID> {

    List<RoomTypeImage> findAllByRoomType_IdOrderBySortOrderAsc(UUID roomTypeId);

    Optional<RoomTypeImage> findByIdAndRoomType_Id(UUID id, UUID roomTypeId);
}