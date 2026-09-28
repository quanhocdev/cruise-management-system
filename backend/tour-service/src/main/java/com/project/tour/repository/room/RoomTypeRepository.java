package com.project.tour.repository.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.tour.model.admin.RoomType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomTypeRepository
                extends JpaRepository<RoomType, UUID> {

        boolean existsByNameIgnoreCase(String name);

        boolean existsByNameIgnoreCaseAndIdNot(
                        String name,
                        UUID excludedRoomTypeId);

        Optional<RoomType> findByNameIgnoreCase(String name);

        List<RoomType> findAllByOrderByNameAsc();

        // Lấy danh sách hạng phòng thuộc một Cruise cụ thể
        @Query("SELECT DISTINCT r.roomType FROM Room r JOIN r.cruiseDeck cd WHERE cd.cruise.id = :cruiseId")
        List<RoomType> findRoomTypesByCruiseId(@Param("cruiseId") UUID cruiseId);
}