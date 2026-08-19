package com.project.tour.repository.tour;

import com.project.tour.model.ServiceTour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceTourAssignmentRepository
                extends JpaRepository<ServiceTour, UUID> {

        Optional<ServiceTour> findByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        List<ServiceTour> findAllByTourIdOrderByCreatedAtAsc(
                        UUID tourId);

        void deleteByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);
}