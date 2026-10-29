
package com.project.tour.repository.scheduler;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.tour.model.scheduler.TourRoute;

@Repository
public interface TourRouteRepository extends JpaRepository<TourRoute, UUID> {

    Optional<TourRoute> findByTour_Id(UUID tourId);

    boolean existsByTour_Id(UUID tourId);

    void deleteByTour_Id(UUID tourId);
}
