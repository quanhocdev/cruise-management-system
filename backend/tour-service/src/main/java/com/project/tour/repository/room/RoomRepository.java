package com.project.tour.repository.room;

import com.project.tour.model.Room;
import com.project.tour.model.enums.RoomStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

        boolean existsByCruiseDeck_IdAndCodeIgnoreCase(
                        UUID deckId,
                        String code);

        boolean existsByCruiseDeck_IdAndCodeIgnoreCaseAndIdNot(
                        UUID deckId,
                        String code,
                        UUID excludedRoomId);

        Optional<Room> findByIdAndCruiseDeck_Id(
                        UUID id,
                        UUID deckId);

        List<Room> findAllByCruiseDeck_IdOrderByCodeAsc(
                        UUID deckId);

        List<Room> findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(
                        UUID deckId,
                        RoomStatus status);

        boolean existsByRoomType_Id(UUID roomTypeId);

        @Query("""
                SELECT r FROM Room r
                JOIN FETCH r.cruiseDeck d
                JOIN FETCH r.roomType rt
                WHERE d.cruise.id = :cruiseId
                  AND d.status = com.project.tour.model.enums.cruise.CruiseDeckStatus.ACTIVE
                  AND r.status = com.project.tour.model.enums.RoomStatus.ACTIVE
                ORDER BY d.deckNumber ASC, r.code ASC
                """)
        List<Room> findActiveRoomsByCruiseId(@Param("cruiseId") UUID cruiseId);
}
