package com.project.tour.repository.cruise;

import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CruiseDeckRepository
                extends JpaRepository<CruiseDeck, UUID> {

        boolean existsByCruise_IdAndDeckNumber(
                        UUID cruiseId,
                        Integer deckNumber);

        boolean existsByCruise_IdAndDeckNumberAndIdNot(
                        UUID cruiseId,
                        Integer deckNumber,
                        UUID excludedDeckId);

        Optional<CruiseDeck> findByIdAndCruise_Id(
                        UUID id,
                        UUID cruiseId);

        List<CruiseDeck> findAllByCruise_IdOrderByDeckNumberAsc(
                        UUID cruiseId);

        List<CruiseDeck> findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                        UUID cruiseId,
                        CruiseDeckStatus status);
}
