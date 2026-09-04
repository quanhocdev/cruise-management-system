package com.project.convenience.repository;

import com.project.convenience.model.HistoryProductTour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryProductTourRepository
        extends JpaRepository<HistoryProductTour, UUID> {

    boolean existsByTourId(UUID tourId);

    List<HistoryProductTour> findAllByOrderByCompletedAtDesc();
}