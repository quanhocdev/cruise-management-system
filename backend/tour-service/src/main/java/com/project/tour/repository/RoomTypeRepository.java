package com.project.tour.repository;

import com.project.tour.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomTypeRepository
    extends JpaRepository<RoomType, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
        String name,
        UUID excludedRoomTypeId
    );

    Optional<RoomType> findByNameIgnoreCase(String name);

    List<RoomType> findAllByOrderByNameAsc();
}
