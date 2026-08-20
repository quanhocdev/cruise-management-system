package com.project.tour.repository.tour;

import com.project.tour.model.ServiceTour;
import com.project.tour.model.enums.convenience.ServiceTourStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceTourAssignmentRepository
                extends JpaRepository<ServiceTour, UUID> {

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        Optional<ServiceTour> findById(UUID id);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        Optional<ServiceTour> findByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByTourIdOrderByCreatedAtAsc(
                        UUID tourId);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByStatusOrderByCreatedAtAsc(
                        ServiceTourStatus status);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId,
                        ServiceTourStatus status);

        void deleteByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);
}