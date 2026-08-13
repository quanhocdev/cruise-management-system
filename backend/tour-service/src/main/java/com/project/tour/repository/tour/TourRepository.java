package com.project.tour.repository.tour;

import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            UUID excludedTourId);

    Optional<Tour> findByCodeIgnoreCase(String code);

    List<Tour> findAllByOrderByNameAsc();

    List<Tour> findAllByCruise_IdOrderByNameAsc(UUID cruiseId);

    List<Tour> findAllByStatusTripOrderByNameAsc(
            TourStatusTrip statusTrip);

    List<Tour> findAllByStatusBookingOrderByNameAsc(
            TourBookingStatus statusBooking);
}