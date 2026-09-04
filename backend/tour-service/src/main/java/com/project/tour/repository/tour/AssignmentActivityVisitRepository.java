package com.project.tour.repository.tour;

import com.project.tour.model.AssignmentActivityVisit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentActivityVisitRepository
        extends JpaRepository<AssignmentActivityVisit, UUID> {

    // =====================================================
    // GET ALL BY TOUR
    // ===================================================
    // ==

    List<AssignmentActivityVisit> findAllByOrderByCreatedAtAsc();

    List<AssignmentActivityVisit> findAllByTourIdOrderByCreatedAtAsc(
            UUID tourId);

    // =====================================================
    // FIND BY TOUR + SCHEDULE STOP
    // =====================================================

    Optional<AssignmentActivityVisit> findByTourIdAndScheduleStopId(
            UUID tourId,
            UUID scheduleStopId);

    // =====================================================
    // EXISTS
    // =====================================================

    boolean existsByTourIdAndScheduleStopId(
            UUID tourId,
            UUID scheduleStopId);

    // =====================================================
    // DELETE
    // =====================================================

    void deleteByTourIdAndScheduleStopId(
            UUID tourId,
            UUID scheduleStopId);
}