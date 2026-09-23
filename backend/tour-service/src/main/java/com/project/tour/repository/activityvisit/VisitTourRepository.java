package com.project.tour.repository.activityvisit;

import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.model.activityvisit.enums.VisitTourStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
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

        Optional<VisitTour> findByScheduleStopId(UUID scheduleStopId);

        // =====================================================
        // GET BY TOUR
        // =====================================================

        List<VisitTour> findAllByTourIdOrderByStartTimeAsc(
                        UUID tourId);

        // =====================================================
        // GET BY TOUR + SCHEDULE STOP
        // =====================================================

        Optional<VisitTour> findByTourIdAndScheduleStopId(
                        UUID tourId,
                        UUID scheduleStopId);

        boolean existsByTourIdAndScheduleStopId(
                        UUID tourId,
                        UUID scheduleStopId);

        void deleteByTourIdAndScheduleStopId(
                        UUID tourId,
                        UUID scheduleStopId);

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
}
