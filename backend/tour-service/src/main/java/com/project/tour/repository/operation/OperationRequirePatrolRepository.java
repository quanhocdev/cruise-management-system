
package com.project.tour.repository.operation;

import com.project.tour.model.OperationRequirePatrol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRequirePatrolRepository
        extends JpaRepository<OperationRequirePatrol, UUID> {

    List<OperationRequirePatrol> findAllByTourId(UUID tourId);

    Optional<OperationRequirePatrol> findByTourIdAndCruiseDeck_Id(
            UUID tourId,
            UUID cruiseDeckId);
}
