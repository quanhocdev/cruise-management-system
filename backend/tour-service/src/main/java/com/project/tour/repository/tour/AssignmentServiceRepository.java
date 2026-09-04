package com.project.tour.repository.tour;

import com.project.tour.model.AssignmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentServiceRepository extends JpaRepository<AssignmentService, UUID> {

    List<AssignmentService> findAllByOrderByCreatedAtAsc();

    List<AssignmentService> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

    Optional<AssignmentService> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    boolean existsByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    @Modifying
    @Query("DELETE FROM AssignmentService a WHERE a.tourId = :tourId AND a.cruiseAreaId = :cruiseAreaId")
    void deleteByTourIdAndCruiseAreaId(@Param("tourId") UUID tourId, @Param("cruiseAreaId") UUID cruiseAreaId);
}