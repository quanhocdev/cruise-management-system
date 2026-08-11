package com.project.tour.repository;

import com.project.tour.model.Room;
import com.project.tour.model.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    boolean existsByCruiseDeck_IdAndCodeIgnoreCase(UUID deckId, String code);

    boolean existsByCruiseDeck_IdAndCodeIgnoreCaseAndIdNot(
        UUID deckId,
        String code,
        UUID excludedRoomId
    );

    Optional<Room> findByIdAndCruiseDeck_Id(UUID id, UUID deckId);

    List<Room> findAllByCruiseDeck_IdOrderByCodeAsc(UUID deckId);

    List<Room> findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(
        UUID deckId,
        RoomStatus status
    );

    boolean existsByRoomType_Id(UUID roomTypeId);
}
