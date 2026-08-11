package com.project.tour.repository;

import com.project.tour.model.CruiseArea;
import com.project.tour.model.enums.CruiseAreaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CruiseAreaRepository extends JpaRepository<CruiseArea, UUID> {
    boolean existsByCruiseDeck_IdAndNameIgnoreCase(UUID deckId, String name);
    boolean existsByCruiseDeck_IdAndNameIgnoreCaseAndIdNot(
        UUID deckId,
        String name,
        UUID excludedAreaId
    );
    Optional<CruiseArea> findByIdAndCruiseDeck_Id(UUID id, UUID deckId);
    List<CruiseArea> findAllByCruiseDeck_IdOrderByNameAsc(UUID deckId);
    List<CruiseArea> findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
        UUID deckId,
        CruiseAreaStatus status
    );
}
