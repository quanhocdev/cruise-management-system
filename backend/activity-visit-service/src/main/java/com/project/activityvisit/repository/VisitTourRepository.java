package com.project.activityvisit.repository;

import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.model.enums.VisitTourStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitTourRepository
                extends JpaRepository<VisitTour, UUID> {

        // =====================================================
        // GET ALL
        // =====================================================

        List<VisitTour> findAllByOrderByCreatedAtDesc();

        // =====================================================
        // GET BY ID
        // =====================================================

        @Override
        Optional<VisitTour> findById(UUID id);

        // =====================================================
        // GET BY SCHEDULE STOP
        // =====================================================

        List<VisitTour> findAllByScheduleStopIdOrderByStartTimeAsc(
                        UUID scheduleStopId);

        // =====================================================
        // GET BY TOUR
        // =====================================================

        List<VisitTour> findAllByTourIdOrderByStartTimeAsc(
                        UUID tourId);

        // =====================================================
        // GET BY TOUR + STATUS
        // =====================================================

        List<VisitTour> findAllByTourIdAndStatusOrderByStartTimeAsc(
                        UUID tourId,
                        VisitTourStatus status);

        // =====================================================
        // SHORE CONFIGURATION
        // =====================================================

        List<VisitTour> findAllByScheduleStopIdInOrderByStartTimeAsc(
                        Collection<UUID> scheduleStopIds);

        List<VisitTour> findAllByScheduleStopIdInAndStatusOrderByStartTimeAsc(
                        Collection<UUID> scheduleStopIds,
                        VisitTourStatus status);

        boolean existsByTourIdAndScheduleStopId(
                        UUID tourId,
                        UUID scheduleStopId);
}