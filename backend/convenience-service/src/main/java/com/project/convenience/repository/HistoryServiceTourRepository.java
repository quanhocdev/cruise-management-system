package com.project.convenience.repository;

import com.project.convenience.model.HistoryServiceTour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryServiceTourRepository
        extends JpaRepository<HistoryServiceTour, UUID> {

    boolean existsByTourId(UUID tourId);

    List<HistoryServiceTour> findAllByOrderByCompletedAtDesc();
}