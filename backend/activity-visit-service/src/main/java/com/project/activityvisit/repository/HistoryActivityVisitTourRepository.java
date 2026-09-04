package com.project.activityvisit.repository;

import com.project.activityvisit.model.HistoryActivityVisitTour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoryActivityVisitTourRepository
        extends JpaRepository<HistoryActivityVisitTour, UUID> {

    boolean existsByTourId(UUID tourId);

    List<HistoryActivityVisitTour> findAllByOrderByCompletedAtDesc();
}